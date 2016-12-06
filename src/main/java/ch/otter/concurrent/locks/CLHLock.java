package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;

/**
 * Created by feliceserena on 06.12.16.
 */

/**
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class CLHLock extends AbstractLock {
    // last node of queue
    private AtomicReference<QNode> tail = new AtomicReference<>(new QNode());
    private ThreadLocal<QNode> myNode = ThreadLocal.withInitial(QNode::new);
    private ThreadLocal<QNode> predecessor = ThreadLocal.withInitial(() -> null);

    @Override
    public void lock() {
        QNode my = myNode.get();
        my.locked = true;
        QNode pred = tail.getAndSet(my);
        predecessor.set(pred);
        while(pred.locked);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

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
        myNode.get().locked = false;
        myNode.set(predecessor.get());
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    class QNode {
        // true: acquired lock or waiting for lock
        // false: lock released
        volatile boolean locked = false;
    }
}
