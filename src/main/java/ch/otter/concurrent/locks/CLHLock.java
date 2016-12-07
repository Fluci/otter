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
        my.state = STATE_WAITING;
        QNode pred = tail.getAndSet(my);
        predecessor.set(pred);
        while(pred.state != STATE_RELEASED);
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
        myNode.get().state = STATE_RELEASED;
        myNode.set(predecessor.get());
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    // lock released
    short STATE_RELEASED = 0;

    // acquire lock, wait for lock
    short STATE_WAITING = 1;
    class QNode {
        volatile short state = STATE_RELEASED;
    }
}
