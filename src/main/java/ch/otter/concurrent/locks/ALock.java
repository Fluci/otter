package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

/**
 * Created by feliceserena on 05.12.16.
 *
 * a simple array-based queue lock
 *
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class ALock extends AbstractLock {
    private final ThreadLocal<Integer> slotIndex = ThreadLocal.withInitial(() -> -1);

    private final AtomicInteger tail = new AtomicInteger(0);
    private volatile byte[] flags;

    // How many flags can be put in one cache line?
    private static int FLAGS_CACHE_LINE = 64;

    private final int capacity;

    private final static byte FLAG_WAITING = 0;
    private final static byte FLAG_HAS_LOCK = 1;
    private final static byte FLAG_ABANDONED = 3;

    private void setFlag(int i, byte val) {
        flags[i * FLAGS_CACHE_LINE] = val;
    }
    private byte getFlag(int i) {
        return flags[i * FLAGS_CACHE_LINE];
    }

    ALock(int capacity){
        if(FLAGS_CACHE_LINE < 1) {
            throw new RuntimeException("Invalid FLAGS_CACHE_LINE of " + FLAGS_CACHE_LINE + ", must be at least 1");
        }
        this.capacity = capacity;
        flags = new byte[this.capacity * FLAGS_CACHE_LINE];
        setFlag(0, FLAG_HAS_LOCK);
        for(int i = 1; i < this.capacity; i += 1) {
            setFlag(i, FLAG_WAITING);
        }
    }

    private int enqueue(){
        int id = tail.getAndIncrement() % capacity;
        slotIndex.set(id);

        // should rarely happen
        while(getFlag(id) == FLAG_ABANDONED);

        return id;
    }

    @Override
    public void lock() {
        int id = enqueue();
        while (getFlag(id) == FLAG_WAITING);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        int id = enqueue();
        if (getFlag(id) == FLAG_HAS_LOCK) {
            // got lock
            return true;
        }
        // recover
        setFlag(id, FLAG_ABANDONED);
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        int id = enqueue();
        long stopAt = getStopAt(time, unit);
        while (getFlag(id) == FLAG_WAITING) {
            if(stopAtExpired(stopAt)) {
                setFlag(id, FLAG_ABANDONED);
                return false;
            }
        }
        return true;
    }

    @Override
    public void unlock() {
        int id = slotIndex.get();
        setFlag(id, FLAG_WAITING);
        int nextId = (id + 1) % capacity;
        while(getFlag(nextId) == FLAG_ABANDONED){
            setFlag(nextId, FLAG_WAITING);
            nextId = (nextId + 1) % capacity;
        }
        setFlag(nextId, FLAG_HAS_LOCK);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
