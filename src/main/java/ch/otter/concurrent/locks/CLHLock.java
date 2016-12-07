package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;

/**
 * Created by feliceserena on 06.12.16.
 */

/**
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 *
 * Implicit queue, spins on node of predecessor.
 * When normally locking, nodes' ownership travels from predecessor to successor.
 */
public class CLHLock extends AbstractLock {
    // last node of queue
    private AtomicReference<QNode> tail = new AtomicReference<>(new QNode());
    private ThreadLocal<QNode> myNode = ThreadLocal.withInitial(QNode::new);

    private QNode enqueue(){
        QNode my = myNode.get();

        if(my.state != STATE_RELEASED){
            throw new RuntimeException("Invalid state " + my.state + " encountered while enqueuing.");
        }
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
            // we have to wait for other lock to either release lock
            // or abandon it
            while (state == STATE_WAITING) {
                state = pred.state;
            }
            if(state == STATE_RELEASED) {
                my.predecessor = pred;
                return;
            }
            // state == STATE_ABANDONED
            // just skip this node and let it be garbage collected
            pred = pred.predecessor;
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        QNode my = enqueue();
        if(my.predecessor.state == STATE_RELEASED) {
            // got lock
            return true;
        }
        // failed
        my.state = STATE_ABANDONED;
        myNode.set(new QNode());
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
                    myNode.set(new QNode());
                    return false;
                }
                state = pred.state;
            }
            if(state == STATE_RELEASED) {
                my.predecessor = pred;
                return true;
            }
            // state == STATE_ABANDONED
            pred = pred.predecessor;
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
    byte STATE_RELEASED = 0;

    // acquired lock, wait for lock
    byte STATE_WAITING = 1;

    // No more interest in acquiring the lock
    byte STATE_ABANDONED = 2;

    class QNode {
        volatile byte state = STATE_RELEASED;
        volatile QNode predecessor = null;
    }
}
