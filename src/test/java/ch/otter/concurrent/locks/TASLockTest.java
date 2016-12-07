package ch.otter.concurrent.locks;

import org.junit.jupiter.api.Test;

/**
 * Created by feliceserena on 05.12.16.
 */
class TASLockTest {
    @Test
    void testSingleThreadLocking() {
        LockInterfaceTests.testSimpleLock(new TASLock(), 1);
    }

    @Test
    void testTwoThreadLocking() {
        LockInterfaceTests.testSimpleLock(new TASLock(), 2);
    }

    @Test
    void testManyThreadLocking4() {
        LockInterfaceTests.testSimpleLock(new TASLock(), 4, 500000);
    }

    @Test
    void testManyThreadLocking8() {
        LockInterfaceTests.testSimpleLock(new TASLock(), 8, 200000);
    }

    @Test
    void testManyThreadLocking16() {
        LockInterfaceTests.testSimpleLock(new TASLock(), 16, 50000);
    }

    @Test
    void testTryLockSimpleTrue() {
        LockInterfaceTests.testTryLockSimpleTrue(new TASLock());
    }

    @Test
    void testTryLockSimpleFalse(){
        LockInterfaceTests.testTryLockSimpleFalse(new TASLock());
    }

    @Test
    void testTryLockSimpleFalseSingleBlock() {
        LockInterfaceTests.testTryLockSimpleFalseSingleBlocker(new TASLock());
    }

    @Test
    void testTryLockTimeOutTrue() {
        LockInterfaceTests.testTryLockTimeOutTrue(new TASLock());
    }

    @Test
    void testTryLockTimeOutFalse(){
        LockInterfaceTests.testTryLockTimeOutFalse(new TASLock());
    }

    @Test
    void testTryLockTimeOutFalseSingleBlocker(){
        LockInterfaceTests.testTryLockTimeOutFalseSingleBlocker(new TASLock());
    }
}