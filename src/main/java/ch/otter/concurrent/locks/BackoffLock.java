package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

/**
 * Created by feliceserena on 05.12.16.
 */

/**
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class BackoffLock extends AbstractLock {
    private AtomicBoolean lockTaken = new AtomicBoolean(false);
    private static final int MIN_WAIT = 1;
    private static final int MAX_WAIT = 1000;

    @Override
    public void lock() {
        while(true) {
            try {
                lockInterruptibly();
                return;
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        Backoff waiter = new Backoff(MIN_WAIT, MAX_WAIT);
        while(true) {
            while(lockTaken.get());
            if(!lockTaken.getAndSet(true)) {
                return;
            } else {
                waiter.backoff();
            }
        }

    }

    @Override
    public boolean tryLock() {
        return !lockTaken.getAndSet(true);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        Backoff waiter = new Backoff(MIN_WAIT, MAX_WAIT);
        long stopAt = getStopAt(time, unit);
        while(true) {
            while(lockTaken.get()){
                if(stopAtExpired(stopAt)) {
                    return false;
                }
            }
            if(!lockTaken.getAndSet(true)) {
                return true;
            } else {
                waiter.backoff();
            }
        }
    }

    @Override
    public void unlock() {
        lockTaken.set(false);
    }

    @Override
    public Condition newCondition() {
        return null;
    }


}
