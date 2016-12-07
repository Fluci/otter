package ch.otter.concurrent.locks;

import org.junit.jupiter.api.Test;

/**
 * Created by feliceserena on 05.12.16.
 */
class TicketLockTest {
    @Test
    void testSingleThreadLocking() {
        LockInterfaceTests.testSimpleLock(new TicketLock(), 1);
    }

    @Test
    void testTwoThreadLocking() {
        LockInterfaceTests.testSimpleLock(new TicketLock(), 2);
    }

    @Test
    void testManyThreadLocking4() {
        LockInterfaceTests.testSimpleLock(new TicketLock(), 4, 500000);
    }

    @Test
    void testManyThreadLocking8() {
        LockInterfaceTests.testSimpleLock(new TicketLock(), 8, 200000);
    }

    @Test
    void testManyThreadLocking16() {
        LockInterfaceTests.testSimpleLock(new TicketLock(), 16, 10000);
    }

    @Test
    void testTryLockSimpleTrue() {
        LockInterfaceTests.testTryLockSimpleTrue(new TicketLock());
    }

    @Test
    void testTryLockSimpleFalse(){
        LockInterfaceTests.testTryLockSimpleFalse(new TicketLock());
    }

    @Test
    void testTryLockSimpleFalseSingleBlock() {
        LockInterfaceTests.testTryLockSimpleFalseSingleBlocker(new TicketLock());
    }

    @Test
    void testTryLockTimeOutTrue() {
        LockInterfaceTests.testTryLockTimeOutTrue(new TicketLock());
    }

    @Test
    void testTryLockTimeOutFalse(){
        LockInterfaceTests.testTryLockTimeOutFalse(new TicketLock());
    }

    @Test
    void testTryLockTimeOutFalseSingleBlocker(){
        LockInterfaceTests.testTryLockTimeOutFalseSingleBlocker(new TicketLock());
    }
}