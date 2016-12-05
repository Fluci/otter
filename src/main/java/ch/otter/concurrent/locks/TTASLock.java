package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by feliceserena on 05.12.16.
 */
public class TTASLock extends AbstractLock {
    private AtomicBoolean lockTaken = new AtomicBoolean(false);

    @Override
    public void lock() {
        while(true) {
            while(lockTaken.get());
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
