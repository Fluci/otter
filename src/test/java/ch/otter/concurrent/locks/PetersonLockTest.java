package ch.otter.concurrent.locks;

import org.junit.jupiter.api.Test;

/**
 * Created by feliceserena on 05.12.16.
 */
class PetersonLockTest {
    @Test
    void testSingleThreadLocking() {
        LockInterfaceTests.testSimpleLock(new PetersonLock(2), 1);
    }

    @Test
    void testTwoThreadLocking() {
        LockInterfaceTests.testSimpleLock(new PetersonLock(2), 2);
    }

    @Test
    void testManyThreadLocking4() {
        LockInterfaceTests.testSimpleLock(new PetersonLock(4), 4, 500000);
    }

    @Test
    void testManyThreadLocking8() {
        LockInterfaceTests.testSimpleLock(new PetersonLock(8), 8, 200000);
    }

    @Test
    void testManyThreadLocking16() {
        LockInterfaceTests.testSimpleLock(new PetersonLock(16), 16, 10000);
    }

    @Test
    void testTryLockSimpleTrue() {
        LockInterfaceTests.testTryLockSimpleTrue(new PetersonLock(2));
    }

    @Test
    void testTryLockSimpleFalse() {
        LockInterfaceTests.testTryLockSimpleFalse(new PetersonLock(2));
    }

    @Test
    void testTryLockSimpleFalseSingleBlock() {
        LockInterfaceTests.testTryLockSimpleFalseSingleBlocker(new PetersonLock(2));
    }

    @Test
    void testTryLockTimeOutTrue() {
        LockInterfaceTests.testTryLockTimeOutTrue(new PetersonLock(2));
    }

    @Test
    void testTryLockTimeOutFalse(){
        LockInterfaceTests.testTryLockTimeOutFalse(new PetersonLock(2));
    }

    @Test
    void testTryLockTimeOutFalseSingleBlocker(){
        LockInterfaceTests.testTryLockTimeOutFalseSingleBlocker(new PetersonLock(2));
    }
}