package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by feliceserena on 05.12.16.
 *
 */

/**
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class TTASLock extends AbstractLock {
    private AtomicBoolean lockTaken = new AtomicBoolean(false);

    @Override
    public void lock() {
        while(true) {
            // local spinning
            while(lockTaken.get());
            // test memory
            if(!lockTaken.getAndSet(true)){
                return;
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        return !lockTaken.getAndSet(true);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
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
