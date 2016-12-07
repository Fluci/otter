package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 06.12.16.
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;

/**
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 *
 * Sets `next` field of predecessor, spins on own QNode.
 */
public class MCSLock extends AbstractLock {
    private final AtomicReference<QNode> tail = new AtomicReference<>();
    private final ThreadLocal<QNode> myNode = ThreadLocal.withInitial(QNode::new);

    @Override
    public void lock() {
        QNode my = myNode.get();

        QNode pred = tail.getAndSet(my);
        if(pred == null) {
            return;
        }
        my.setState(STATE_LOCKED);
        // unlocking node might have to wait for this
        pred.next = my;
        while(my.getState() == STATE_LOCKED);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        QNode my = myNode.get();
        if(tail.compareAndSet(null, my)){
            return true;
        }
        return false;

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        QNode my = myNode.get();
        long stopAt = getStopAt(time, unit);
        do {
        if(tail.compareAndSet(null, my)){
            return true;
        }} while(!stopAtExpired(stopAt));
        return false;
    }

    @Override
    public void unlock() {
        QNode my = myNode.get();
        if (my.next == null) {
            if (tail.compareAndSet(my, null)) {
                return;
            }
            // wait for thread to completely insert itself
            while (my.next == null) ;
        }

        my.next.setState(STATE_RELEASED);
        my.next = null;
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    private final static boolean STATE_RELEASED = false;
    private final static boolean STATE_LOCKED = true;

    private class QNode {
        volatile QNode next = null;
        private volatile boolean state = STATE_RELEASED;
        boolean getState() {
            return state;
        }
        void setState(boolean s) {
            state = s;
        }
    }
}
