package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private volatile boolean[] flags;
    private static int FLAGS_CACHE_LINE = 64; // size of booleans are JVM dependent, this is a heuristic
    private final int threadCount;

    private void setFlag(int i, boolean val) {
        flags[i * FLAGS_CACHE_LINE] = val;
    }
    private boolean getFlag(int i) {
        return flags[i * FLAGS_CACHE_LINE];
    }

    ALock(int capacity){
        this.threadCount = capacity;
        flags = new boolean[threadCount * FLAGS_CACHE_LINE];
        setFlag(0, true);
    }

    @Override
    public void lock() {
        int id = tail.getAndIncrement() % threadCount;
        slotIndex.set(id);
        int i = 0;
        while (!getFlag(id)) {
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
        setFlag(id, false);
        setFlag((id + 1) % threadCount, true);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
