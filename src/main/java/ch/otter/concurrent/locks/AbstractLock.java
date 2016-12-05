package ch.otter.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by feliceserena on 05.12.16.
 */
abstract class AbstractLock implements Lock {
    long getStopAt(long time, TimeUnit unit){
        return System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit);
    }
    boolean stopAtExpired(long stopAt) {
        return stopAt > System.currentTimeMillis();
    }
}
