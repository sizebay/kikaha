package kikaha.cloud.metrics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * Basic meter metricManager. It implies in lower memory footprint
 * than Codahale's MetricManager, but it has less features too.
 */
@RequiredArgsConstructor
public class FairMeter implements ReadOnlyData<FairMeter.MeterData>, Metric {

    final AtomicLong counter = new AtomicLong(0l);
    final AtomicLong totalResponseTime = new AtomicLong(0l);
    final AtomicLong startTime = new AtomicLong(System.nanoTime());

    @Getter final String name;
    @Getter final String namespace;

    public MeterData getData() {
        long elapsedTime = System.nanoTime();
        return new MeterData(name, namespace,
            counter.getAndSet(0),
            totalResponseTime.getAndSet( 0 ),
            elapsedTime - startTime.getAndSet(elapsedTime)
        );
    }

    /**
     * Increments the counter value of this meter by 1.
     */
    public void mark() {
        fairIncrementCounter( counter, 1 );
    }

    /**
     * Increments the elapsed time counter of this meter by the amount defined by {@code elapsedTime}.
     * @param elapsedTime
     */
    public void addElapsedTime( long elapsedTime ){
        fairIncrementCounter( totalResponseTime, elapsedTime );
    }

    /**
     * use fair compareAndSet algorithm to increment counter. This code was based on
     * Dave Dice, Danny Hendler and Ilya Mirsky research.
     *
     * @param counter
     * @param amount
     * @implNote http://arxiv.org/abs/1305.5800
     */
    private void fairIncrementCounter( final AtomicLong counter, final long amount ){
        for (;;) {
            long current = counter.get();
            long next = current + amount;
            if (compareAndSet(counter, current, next))
                return;
        }
    }

    /**
     * Fair compareAndSet based on Dave Dice, Danny Hendler and Ilya Mirsky research.
     *
     * @param current
     * @param next
     * @return true if could set the new value
     * @implNote http://arxiv.org/abs/1305.5800
     */
    private boolean compareAndSet(final AtomicLong counter, final long current, final long next) {
        if (counter.compareAndSet(current, next)) {
            return true;
        } else {
            LockSupport.parkNanos(1);
            return false;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class MeterData implements Metric {

        private static final double NS_SEC_RATE = 0.000000001d;

        final String name;
        final String namespace;
        final long counter;
        final long totalResponseTime;
        final long elapsedTimeSinceLastSnapshot;

        public final double rate() {
            return counter / (elapsedTimeSinceLastSnapshot * NS_SEC_RATE);
        }

        public final double responseTime() {
            return totalResponseTime / counter;
        }
    }
}
