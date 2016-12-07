package ch.otter.concurrent.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by feliceserena on 05.12.16.
 */
public class LockInterfaceTests {

    static void testSimpleLock(Lock lock) {
        testSimpleLock(lock, 2);
    }

    static void testSimpleLock(Lock lock, int threadCount) {
        testSimpleLock(lock, threadCount, 1000*1000);
    }

    // TODO: make no progress timeout (read value, check every second, if not changed after timeout -> fail)
    static void testSimpleLock(Lock lock, int threadCount, final long eachWorkSize) {
        if(threadCount < 1){
            assertTrue(false, "invalid number of requested threads: " + threadCount);
        }
        if(threadCount == 1) {
            for(int i = 0; i < eachWorkSize; i += 1) {
                lock.lock();
                lock.unlock();
            }
            return;
        }
        long expectedResult = threadCount*eachWorkSize;
        List<Thread> threads = new ArrayList<>(threadCount);
        Counter runnable = new Counter(lock, eachWorkSize);
        for(int i = 0; i < threadCount; i += 1) {
            threads.add(new Thread(runnable));
        }
        for(Thread t : threads) {
            t.start();
        }
        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            // Rethrow for junit
            throw new RuntimeException(e);
        }
        assertEquals(expectedResult, Counter.sharedCounter);
    }

    /// try to acquire lock, should immediately work
    static void testTryLockSimpleTrue(Lock lock) {
        testTryLockSimpleTrue(lock, 1000);
    }

    static void testTryLockSimpleTrue(Lock lock, int tries) {
        for(int i = 0; i < tries; i += 1) {
            assertTrue(lock.tryLock(), "Failed at try " + i + "/" + tries);
            lock.unlock();
        }
    }

    static void testTryLockSimpleFalse(Lock lock) {
        testTryLockSimpleFalse(lock, 1000);
    }
    static void testTryLockSimpleFalse(final Lock lock, int tries) {
        // some set up for test
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final Semaphore stopThread = new Semaphore(0);
        Runnable runnable = new Runnable(){
            public void run() {
                lock.lock();
                try {
                    barrier.await();
                    stopThread.acquire();
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                    assertTrue(false);
                } finally {
                    lock.unlock();
                }
            }
        };
        for(int i = 0; i < tries; i += 1) {
            new Thread(runnable).start();
            try {
                barrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
                stopThread.release();
                assertTrue(false, "Failed at try " + i + "/" + tries);
            }

            // test: tryLock should return immediately with false
            try {
                boolean val = lock.tryLock();
                assertFalse(val, "Failed at try " + i + "/" + tries + ", tryLock returned " + val + " instead of false.");
            } catch (Exception e) {
                // assert threw, lock was acquired
                lock.unlock();
                throw new RuntimeException(e);
            } finally {
                stopThread.release();
            }
        }
    }
    static void testTryLockTimeOutTrue(final Lock lock) {
        testTryLockTimeOutTrue(lock, 10);
    }
    static void testTryLockTimeOutTrue(final Lock lock, int tries) {
        // get system warm
        lock.lock();
        lock.unlock();
        // test
        for(int i = 0; i < tries; i += 1) {
            try {
                boolean val = lock.tryLock(10, TimeUnit.DAYS);
                // if this throws, lock wasn't acquired, so all fine
                assertTrue(val);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            lock.unlock();
        }
    }

    static void testTryLockTimeOutFalse(final Lock lock) {
        testTryLockTimeOutFalse(lock, 200);
    }

    static void testTryLockTimeOutFalse(final Lock lock, int tries) {
        testTryLockTimeOutFalse(lock, tries, 2, TimeUnit.MILLISECONDS);
    }

    static void testTryLockTimeOutFalse(final Lock lock, int tries, int time, TimeUnit unit) {
        // some set up for test
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final Semaphore stopThread = new Semaphore(0);
        Runnable runnable = new Runnable(){
            public void run() {
                lock.lock();
                try {
                    barrier.await();
                    stopThread.acquire();
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                    assertTrue(false);
                } finally {
                    lock.unlock();
                }
            }
        };
        long timeInMillis = TimeUnit.MILLISECONDS.convert(time, unit);
        for(int i = 0; i < tries; i += 1) {
            new Thread(runnable).start();
            try {
                barrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
                stopThread.release();
                assertTrue(false, "Failed at try " + i + "/" + tries);
            }

            // test: tryLock should return after `time` with false
            long duration;
            try {
                long start = System.currentTimeMillis();
                boolean val = lock.tryLock(time, unit);
                long end = System.currentTimeMillis();
                duration = end - start;

                assertFalse(val, "Failed at try " + i + "/" + tries + " (" + duration + " ms)");
            } catch (Exception e) {
                // assert threw, lock was acquired
                lock.unlock();
                throw new RuntimeException(e);
            } finally {
                stopThread.release();
            }
            assertTrue(timeInMillis <= duration,
                    "TryLock waited only for " + duration + " ms, "
                            + "should have waited at least " + timeInMillis + " ms.");
        }
    }


    private static class Counter implements Runnable {
        private static long sharedCounter = 0;
        private long workSize;
        private final Lock lock;
        Counter(final Lock l, long wSize){
            lock = l;
            workSize = wSize;
            sharedCounter = 0;
        }
        @Override
        public void run() {
            for (int i = 0; i < workSize; i += 1) {
                lock.lock();
                sharedCounter++;
                lock.unlock();
            }
        }
    }
}
