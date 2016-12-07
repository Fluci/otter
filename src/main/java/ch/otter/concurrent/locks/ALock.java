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
    private final ThreadLocal<Integer> slotIndex = ThreadLocal.withInitial(() -> -1);

    private final AtomicInteger tail = new AtomicInteger(0);
    private volatile AtomicIntegerArray flags;

    private final int capacity;

    private final static int FLAG_WAITING = 0;
    private final static int FLAG_HAS_LOCK = 1;
    private final static int FLAG_ABANDONED = 3;

    private boolean compareAndSet(int i, int expect, int update) {
        return flags.compareAndSet(i, expect, update);
    }
    private void setFlag(int i, int val) {
        flags.set(i, val);
    }
    private int getFlag(int i) {
        return flags.get(i);
    }

    ALock(int capacity){
        this.capacity = capacity;
        flags = new AtomicIntegerArray(this.capacity);
        setFlag(0, FLAG_HAS_LOCK);
        for(int i = 1; i < this.capacity; i += 1) {
            setFlag(i, FLAG_WAITING);
        }
    }

    @Override
    public void lock() {
        int id = getNewId();
        slotIndex.set(id);
        compareAndSet(id, FLAG_ABANDONED, FLAG_WAITING);
        while(getFlag(id) == FLAG_WAITING);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        int id = getNewId();
        slotIndex.set(id);
        int flag = getFlag(id);

        if(flag == FLAG_HAS_LOCK) {
            // got lock
            return true;
        }
        // recover
        // If no other thread is running, there are no abandoned flags
        // and our field would be set to waiting
        // so there is somebody working and we return
        if(flag == FLAG_WAITING) {
            setFlag(id, FLAG_ABANDONED);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        int id = getNewId();
        slotIndex.set(id);
        long stopAt = getStopAt(time, unit);
        System.err.println(getFlag(id));
        while (getFlag(id) != FLAG_HAS_LOCK) {
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
        int nextId = getNextId(id);
        while(compareAndSet(nextId, FLAG_ABANDONED, FLAG_WAITING)){
            nextId = getNextId(nextId);
        }
        setFlag(nextId, FLAG_HAS_LOCK);
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    private int getNextId(int id) {
        return (id + 1) % capacity;
    }
    private int getNewId(){
        return tail.getAndIncrement() % capacity;
    }
    private void releaseId(int id) {

    }
}
