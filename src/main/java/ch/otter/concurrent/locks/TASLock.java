package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 05.12.16.
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

/**
 * Implementation of a TASLock
 *
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class TASLock extends AbstractLock {
    private AtomicBoolean lockTaken = new AtomicBoolean(false);
	public void lock() {
	    // stop looping when lock was not taken before
        while(lockTaken.getAndSet(true));
	}

	public void lockInterruptibly() throws InterruptedException {
        lock();
	}

	public boolean tryLock() {
	    return !lockTaken.getAndSet(true);
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long stopAt = getStopAt(time, unit);
        while(lockTaken.getAndSet(true)) {
            if(stopAtExpired(stopAt)){
                return false;
            }
        }
		return true;
	}

    public void unlock() {
        lockTaken.set(false);
    }

    public Condition newCondition() {
        // TODO
        return null;
    }
}
