package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 05.12.16.
 *
 *
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Only for two threads.
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class PetersonLock extends AbstractLock {
    private final AtomicIntegerArray level;
    private final AtomicIntegerArray last_to_enter;
    private final int threadCount;
    PetersonLock(int threadCount){
        if(threadCount < 0) {
            throw new IllegalArgumentException("threadCount must be at least 0.");
        } else if(threadCount < 2) {
            threadCount = 2;
        }
        this.threadCount = threadCount;
        level = new AtomicIntegerArray(threadCount);
        last_to_enter = new AtomicIntegerArray(threadCount-1);
        for(int i = 0; i < threadCount; i+= 1) {
            level.set(i, -1);
        }
    }

    public void lock() {
        int id = getId();
        for(int l = 0; l < threadCount-1; l += 1) {
            level.set(id, l);
            last_to_enter.set(l, id);
            while (last_to_enter.get(l) == id && existsKGreaterL(l, id));
        }
    }

    private boolean existsKGreaterL(int l, int id) {
        for(int k = 0; k < id; k += 1) {
            if(level.get(k) >= l) {
                return true;
            }
        }
        for(int k = id+1; k < threadCount; k += 1) {
            if(level.get(k) >= l) {
                return true;
            }
        }
        return false;
    }

    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    public boolean tryLock() {
        int id = getId();
        for(int l = 0; l < threadCount-1; l += 1) {
            level.set(id, l);
            last_to_enter.set(l, id);
            if (last_to_enter.get(l) == id && existsKGreaterL(l, id)) {
                level.set(id, -1);
                return false;
            }
        }
        return true;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        level.set(getId(), -1);
    }

    public Condition newCondition() {
        return null;
    }

    private int getId() {
        return (int)Thread.currentThread().getId()%threadCount;
    }
}
