package ch.otter.concurrent.locks;

import java.util.Random;

/**
 * Created by feliceserena on 05.12.16.
 */
/**
 * Waits for a random time interval.
 *
 * The original can be found in "The Art of Multiprocessor Programming by Maurice Herlihy & Nir Shavit".
 */
class Backoff {
    final int MIN_WAIT;
    final int MAX_WAIT;
    final Random rand = new Random();
    int limit;

    Backoff(int millis_min, int millis_max) {
        MIN_WAIT = millis_min;
        MAX_WAIT = millis_max;
        limit = MIN_WAIT;
    }

    void backoff() throws InterruptedException {
        int wait = rand.nextInt(limit);
        limit = Math.min(MAX_WAIT, 2*limit);
        Thread.sleep(limit);
    }
}
