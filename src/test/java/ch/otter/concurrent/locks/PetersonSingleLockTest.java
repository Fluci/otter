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
        LockInterfaceTests.testSimpleLock(new PetersonSingleLock(), 2, 3*1000*1000);
    }

    @Test
    void testTryLockSimpleTrue() {
        LockInterfaceTests.testTryLockSimpleTrue(new PetersonSingleLock());
    }

    @Test
    void testTryLockSimpleFalse() {
        LockInterfaceTests.testTryLockSimpleFalse(new PetersonSingleLock());
    }

    @Test
    void testTryLockSimpleFalseSingleBlock() {
        LockInterfaceTests.testTryLockSimpleFalseSingleBlocker(new PetersonSingleLock());
    }

    @Test
    void testTryLockTimeOutTrue() {
        LockInterfaceTests.testTryLockTimeOutTrue(new PetersonSingleLock());
    }

    @Test
    void testTryLockTimeOutFalse(){
        LockInterfaceTests.testTryLockTimeOutFalse(new PetersonSingleLock());
    }

    @Test
    void testTryLockTimeOutFalseSingleBlocker(){
        LockInterfaceTests.testTryLockTimeOutFalseSingleBlocker(new PetersonSingleLock());
    }
}