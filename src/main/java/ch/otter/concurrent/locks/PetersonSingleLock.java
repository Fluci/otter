package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 05.12.16.
 *
 *
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

/**
 * Only for two threads.
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class PetersonSingleLock extends AbstractLock {
    private final ThreadLocal<Integer> id = ThreadLocal.withInitial(() -> new Integer(0));
    private final AtomicBoolean[] ids = new AtomicBoolean[2];
    private final AtomicBoolean[] interested = new AtomicBoolean[2];
    private volatile int lockOwner = 0;

    PetersonSingleLock(){
        interested[0] = new AtomicBoolean(false);
        interested[1] = new AtomicBoolean(false);
        ids[0] = new AtomicBoolean(false);
        ids[1] = new AtomicBoolean(false);
    }

    public void lock() {
        int myId = getId();
        id.set(myId);
        // place own interest
        setInterest(myId, true);
        int otherId = 1 - myId;
        // give priority to other thread
        lockOwner = otherId;
        // if other thread passed lockOwner before this did, otherId is in lockOwner (just written)
        // if the other thread has any interest in entering the critical section interested[otherId] is true
        // other threader returns access by setting intersted[otherId] to false
        // as soon the other thread leaves the critical section, this thread has all time oth the world to enter the critical section
        // The other thread first has to set lockOwner=thisId, which makes it impossible for it to enter the critical region
        // as long as this thread does not release the critical region.
        while(getInterest(otherId) && lockOwner == otherId) {}
    }

    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    public boolean tryLock() {
        int myId = getId();
        id.set(myId);
        setInterest(myId, true);
        int otherId = 1 - myId;
        lockOwner = otherId;
        if(getInterest(otherId) && lockOwner == otherId) {
            setInterest(myId, false);
            releaseId(myId);
            return false;
        }
        return true;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        int myId = getId();
        id.set(myId);
        setInterest(myId, true);
        int otherId = 1 - myId;
        lockOwner = otherId;
        long stopAt = getStopAt(time, unit);
        while(getInterest(otherId) && lockOwner == otherId) {
            if(stopAtExpired(stopAt)) {
                setInterest(myId, false);
                releaseId(myId);
                return false;
            }
        }
        return true;
    }

    public void unlock() {
        int myId = id.get();

        // order of these two statements is crucial
        setInterest(myId, false);
        releaseId(myId);
    }

    public Condition newCondition() {
        return null;
    }

    // helper methods
    // allows for fast experimenting with alternative data-structures

    private boolean getInterest(int anId) {
        return interested[anId].get();
    }

    private void setInterest(int anId, boolean val) {
        interested[anId].set(val);
    }

    private int getId() {
        while(true){
            if (!ids[1].getAndSet(true)) {
                return 1;
            }
            if (!ids[0].getAndSet(true)) {
                return 0;
            }
        }
    }

    private void releaseId(int id) {
        ids[id].set(false);
    }
}
