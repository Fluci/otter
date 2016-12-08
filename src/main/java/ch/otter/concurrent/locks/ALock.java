package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.Condition;

/**
 * Created by feliceserena on 05.12.16.
 *
 * a simple array-based queue lock
 *
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class ALock extends AbstractLock {

    private final AtomicInteger tail = new AtomicInteger(0);

    private final ThreadLocal<Integer> slotIndex = ThreadLocal.withInitial(() -> -1);

    private volatile boolean[] flags;

    private final int capacity;

    // convenience definitions to make life easier

    private final static boolean FLAG_WAITING = false;
    private final static boolean FLAG_HAS_LOCK = true;

    private void setFlag(int i, boolean val) {
        flags[i] = val;
    }

    private boolean getFlag(int i) {
        return flags[i];
    }

    private int getNextId(int id) {
        return (id + 1) % capacity;
    }

    // methods

    ALock(int capacity){
        this.capacity = capacity;

        flags = new boolean[this.capacity];

        setFlag(0, FLAG_HAS_LOCK);
        for(int i = 1; i < this.capacity; i += 1) {
            setFlag(i, FLAG_WAITING);
        }
    }

    @Override
    public void lock() {
        int id = tail.getAndIncrement() % capacity;

        slotIndex.set(id);

        while(getFlag(id) == FLAG_WAITING);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        int id = tail.get();

        if(getFlag(id % capacity) == FLAG_HAS_LOCK && tail.compareAndSet(id, id+1)) {
            slotIndex.set(id % capacity);
            return true;
        }

        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long stopAt = getStopAt(time, unit);

        do {
            int id = tail.get();
            if(getFlag(id % capacity) == FLAG_HAS_LOCK && tail.compareAndSet(id, id+1)){
                slotIndex.set(id % capacity);
                return true;
            }
        } while (!stopAtExpired(stopAt));

        return false;
    }

    @Override
    public void unlock() {
        int id = slotIndex.get();

        setFlag(id, FLAG_WAITING);

        int nextId = getNextId(id);
        setFlag(nextId, FLAG_HAS_LOCK);
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
