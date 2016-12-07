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

    private QNode enqueue(){
        QNode my = myNode.get();

        // wait until thread puts our old predecessor as its own
        while(my.state != STATE_RELEASED);
        my.state = STATE_WAITING;

        // Enqueue, we have our place now, we're in control
        QNode pred = tail.getAndSet(my);

        my.predecessor = pred;
        return my;
    }

    @Override
    public void lock() {
        QNode my = enqueue();
        QNode pred = my.predecessor;
        while(true) {
            short state = pred.state;
            while (state == STATE_WAITING) {state = pred.state;}
            if(state == STATE_RELEASED) {
                return;
            }
            // state == STATE_ABANDONED
            QNode oldPred = pred;
            pred = pred.predecessor;
            // let the other acquire new lock
            oldPred.state = STATE_RELEASED;
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        QNode my = enqueue();
        QNode pred = my.predecessor;
        if(pred.state == STATE_RELEASED) {
            // got lock
            return true;
        }
        // failed
        my.state = STATE_ABANDONED;
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        QNode my = enqueue();
        QNode pred = my.predecessor;
        long stopAt = getStopAt(time, unit);
        while(true) {
            short state = pred.state;
            while (state == STATE_WAITING) {
                if(stopAtExpired(stopAt)){
                    my.state = STATE_ABANDONED;
                    return false;
                }
                state = pred.state;
            }
            if(state == STATE_RELEASED) {
                return true;
            }
            // state == STATE_ABANDONED
            QNode oldPred = pred;
            pred = pred.predecessor;
            // let the other acquire new lock
            oldPred.state = STATE_RELEASED;
        }
    }

    @Override
    public void unlock() {
        QNode my = myNode.get();
        myNode.set(my.predecessor);
        my.predecessor = null;
        my.state = STATE_RELEASED;
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    // lock released
    short STATE_RELEASED = 0;

    // acquire lock, wait for lock
    short STATE_WAITING = 1;

    // No more interest in acquiring the lock
    short STATE_ABANDONED = 2;

    class QNode {
        volatile short state = STATE_RELEASED;
        volatile QNode predecessor = null;
    }
}
