package ch.otter.concurrent.locks;

import org.junit.jupiter.api.Test;

/**
 * Created by feliceserena on 05.12.16.
 */
class PetersonSingleLockTest {
    @Test
    void testSingleThreadLocking() {
        LockInterfaceTests.testSimpleLock(new PetersonSingleLock(), 1);
    }

    @Test
    void testTwoThreadLocking() {
        LockInterfaceTests.testSimpleLock(new PetersonSingleLock(), 2);
    }

    @Test
    void testTryLockSimpleTrue() {
        LockInterfaceTests.testTryLockSimpleTrue(new PetersonSingleLock());
    }

    @Test
    void testTryLockSimpleFalse() {
        LockInterfaceTests.testTryLockSimpleFalse(new PetersonSingleLock());
    }
}