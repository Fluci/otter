package ch.otter.concurrent.locks;

/**
 * Created by feliceserena on 05.12.16.
 */

import org.junit.jupiter.api.Test;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This test only serves as reference to check if the test of `LockInterfaceTests` are ok.
 */

class LockInterfaceTest {
    @Test
    void testSingleThreadLocking() {
        LockInterfaceTests.testSimpleLock(new ReentrantLock(), 1);
    }

    @Test
    void testTwoThreadLocking() {
        LockInterfaceTests.testSimpleLock(new ReentrantLock(), 2);
    }

    @Test
    void testManyThreadLocking4() {
        LockInterfaceTests.testSimpleLock(new ReentrantLock(), 4, 1000000);
    }

    @Test
    void testManyThreadLocking8() {
        LockInterfaceTests.testSimpleLock(new ReentrantLock(), 8, 500000);
    }

    @Test
    void testManyThreadLocking16() {
        LockInterfaceTests.testSimpleLock(new ReentrantLock(), 16, 500000);
    }

    @Test
    void testTryLockSimpleTrue() {
        LockInterfaceTests.testTryLockSimpleTrue(new ReentrantLock());
    }

    @Test
    void testTryLockSimpleFalse() {
        LockInterfaceTests.testTryLockSimpleFalse(new ReentrantLock());
    }

    @Test
    void testTryLockSimpleFalseSingleBlock() {
        LockInterfaceTests.testTryLockSimpleFalseSingleBlocker(new ReentrantLock());
    }

    @Test
    void testTryLockTimeOutTrue() {
        LockInterfaceTests.testTryLockTimeOutTrue(new ReentrantLock());
    }

    @Test
    void testTryLockTimeOutFalse(){
        LockInterfaceTests.testTryLockTimeOutFalse(new ReentrantLock());
    }

    @Test
    void testTryLockTimeOutFalseSingleBlocker(){
        LockInterfaceTests.testTryLockTimeOutFalseSingleBlocker(new ReentrantLock());
    }
}