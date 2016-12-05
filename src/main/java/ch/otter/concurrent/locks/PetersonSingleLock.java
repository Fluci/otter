package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 05.12.16.
 *
 *
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Only for two threads.
 */
public class PetersonSingleLock extends AbstractLock {
    private volatile boolean[] interested = new boolean[2];
    private volatile int lockOwner = 0;
    private final int threadCount = 2;

    public void lock() {
        int id = getId();
        // place own interest
        interested[id] = true;
        int otherId = 1 - id;
        // give priority to other thread
        lockOwner = otherId;
        // if other thread passed lockOwner before this did, otherId is in lockOwner (just written)
        // if the other thread has any interest in entering the critical section interested[otherId] is true
        // other threader returns access by setting intersted[otherId] to false
        // as soon the other thread leaves the critical section, this thread has all time oth the world to enter the critical section
        // The other thread first has to set lockOwner=thisId, which makes it impossible for it to enter the critical region
        // as long as this thread does not release the critical region.
        while(interested[otherId] && lockOwner == otherId) {}
    }

    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    public boolean tryLock() {
        int id = getId();
        interested[id] = true;
        int otherId = 1 - id;
        lockOwner = otherId;
        if(interested[otherId] && lockOwner == otherId) {
            interested[id] = false;
            return false;
        }
        return true;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        interested[getId()] = false;
    }

    public Condition newCondition() {
        return null;
    }

    private int getId() {
        return (int)Thread.currentThread().getId()%threadCount;
    }
}
