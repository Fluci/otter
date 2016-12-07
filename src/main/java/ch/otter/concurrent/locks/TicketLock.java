package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 07.12.16.
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

/**
 * Inspiration: https://www.quora.com/How-does-an-MCS-lock-work
 */
public class TicketLock extends AbstractLock {
    private volatile int nowServing = 0;
    private final AtomicInteger nextTicket = new AtomicInteger(0);

    @Override
    public void lock() {
        int myTicket = nextTicket.getAndIncrement();
        while(myTicket != nowServing);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        return nextTicket.compareAndSet(nowServing, nowServing+1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long stopAt = getStopAt(time, unit);
        do {
            if(nextTicket.compareAndSet(nowServing, nowServing+1)){
                return true;
            }
        } while(!stopAtExpired(stopAt));
        return false;
    }

    @Override
    public void unlock() {
        nowServing += 1;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
