package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 05.12.16.
 *
 *
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Only for two threads.
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class PetersonLock extends AbstractLock {
    private final AtomicBoolean[] ids;
    private final ThreadLocal<Integer> id = ThreadLocal.withInitial(() -> new Integer(-1));
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
        ids = new AtomicBoolean[threadCount];
        for(int i = 0; i < threadCount; i+= 1) {
            level.set(i, -1);
            ids[i] = new AtomicBoolean(false);
        }
    }

    public void lock() {
        int myId = getId();
        id.set(myId);
        for(int l = 0; l < threadCount-1; l += 1) {
            level.set(myId, l);
            last_to_enter.set(l, myId);
            while (last_to_enter.get(l) == myId && existsKGreaterL(l, myId));
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
        int myId = getId();
        id.set(myId);
        for(int l = 0; l < threadCount-1; l += 1) {
            level.set(myId, l);
            last_to_enter.set(l, myId);
            if (last_to_enter.get(l) == myId && existsKGreaterL(l, myId)) {
                level.set(myId, -1);
                releaseId(myId);
                return false;
            }
        }
        return true;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        int myId = id.get();
        level.set(myId, -1);
        releaseId(myId);
    }

    public Condition newCondition() {
        return null;
    }

    private int getId() {
        int myId = id.get();
        if(myId != -1) {
            // first try to acquire old id
            if(!ids[myId].getAndSet(true)){
                return myId;
            }
        }
        while(true) {
            for(int i = 0; i < threadCount; i += 1) {
                if(!ids[i].getAndSet(true)){
                    return i;
                }
            }
        }
    }

    private void releaseId(int anId) {
        ids[anId].set(false);
    }
}
