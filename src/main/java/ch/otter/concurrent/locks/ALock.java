package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

/**
 * Created by feliceserena on 05.12.16.
 *
 * a simple array-based queue lock
 *
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
public class ALock extends AbstractLock {
    private final ThreadLocal<Integer> slotIndex = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    private final AtomicInteger tail = new AtomicInteger(0);
    private volatile byte[] flags;

    // How many flags can be put in one cache line?
    private static int FLAGS_CACHE_LINE = 64;

    private final int threadCount;

    private final static byte FLAG_WAITING = 0;
    private final static byte FLAG_HAS_LOCK = 1;

    private void setFlag(int i, byte val) {
        flags[i * FLAGS_CACHE_LINE] = val;
    }
    private byte getFlag(int i) {
        return flags[i * FLAGS_CACHE_LINE];
    }

    ALock(int capacity){
        if(FLAGS_CACHE_LINE < 1) {
            throw new RuntimeException("Invalid FLAGS_CACHE_LINE of " + FLAGS_CACHE_LINE + ", must be at least 1");
        }
        this.threadCount = capacity;
        flags = new byte[threadCount * FLAGS_CACHE_LINE];
        setFlag(0, FLAG_HAS_LOCK);
        for(int i = 1; i < threadCount; i += 1) {
            setFlag(i, FLAG_WAITING);
        }
    }

    @Override
    public void lock() {
        int id = tail.getAndIncrement() % threadCount;
        slotIndex.set(id);
        int i = 0;
        while (getFlag(id) == FLAG_WAITING) {
            i += 1;
            if(i > 50*1000*1000){
                System.err.println(id + ": yield");
                Thread.yield();
                i = 0;
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock();
    }

    @Override
    public boolean tryLock() {
        // TODO: how???
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // TODO: how???
        return false;
    }

    @Override
    public void unlock() {
        int id = slotIndex.get();
        setFlag(id, FLAG_WAITING);
        setFlag((id + 1) % threadCount, FLAG_HAS_LOCK);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
