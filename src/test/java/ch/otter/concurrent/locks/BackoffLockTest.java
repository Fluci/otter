package ch.otter.concurrent.locks;

import org.junit.jupiter.api.Test;

/**
 * Created by feliceserena on 05.12.16.
 */
class BackoffLockTest {
    @Test
    void testSingleThreadLocking() {
        LockInterfaceTests.testSimpleLock(new BackoffLock(), 1);
    }

    @Test
    void testTwoThreadLocking() {
        LockInterfaceTests.testSimpleLock(new BackoffLock(), 2);
    }

    @Test
    void testManyThreadLocking4() {
        LockInterfaceTests.testSimpleLock(new BackoffLock(), 4, 500000);
    }

    @Test
    void testManyThreadLocking8() {
        LockInterfaceTests.testSimpleLock(new BackoffLock(), 8, 200000);
    }

    @Test
    void testManyThreadLocking16() {
        LockInterfaceTests.testSimpleLock(new BackoffLock(), 16, 50000);
    }

    @Test
    void testTryLockSimpleTrue() {
        LockInterfaceTests.testTryLockSimpleTrue(new BackoffLock());
    }

    @Test
    void testTryLockSimpleFalse() {
        LockInterfaceTests.testTryLockSimpleFalse(new BackoffLock());
    }

    @Test
    void testTryLockSimpleFalseSingleBlock() {
        LockInterfaceTests.testTryLockSimpleFalseSingleBlocker(new BackoffLock());
    }

    @Test
    void testTryLockTimeOutTrue() {
        LockInterfaceTests.testTryLockTimeOutTrue(new BackoffLock());
    }

    @Test
    void testTryLockTimeOutFalse(){
        LockInterfaceTests.testTryLockTimeOutFalse(new BackoffLock());
    }

    @Test
    void testTryLockTimeOutFalseSingleBlocker(){
        LockInterfaceTests.testTryLockTimeOutFalseSingleBlocker(new BackoffLock());
    }
}