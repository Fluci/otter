package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 06.12.16.
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;

/**
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
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
        my.locked = true;
        // unlocking node might have to wait for this
        pred.next = my;
        while(my.locked);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        QNode my = myNode.get();
        if(my.next == null) {
            if (tail.compareAndSet(my, null)) {
                return;
            }
            // wait for thread to insert itself
            while (my.next == null) ;
        }
        // let next thread run
        my.next.locked = false;
        my.next = null;
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    private class QNode {
        volatile QNode next = null;
        volatile boolean locked = false;
    }
}
