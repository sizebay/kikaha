package kikaha.cloud.metrics;

import io.undertow.util.CopyOnWriteMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A registry for metrics.
 */
@Singleton
public class MetricRegistry {

    final Map<String, FairMeter> meters = new CopyOnWriteMap<>();
    final Map<String, Gauge> gauges = new ConcurrentHashMap<>();

    /**
     * Register a metric supplier for a given {@code key}.
     *
     * @param key
     * @param supplier
     * @return this
     */
    public MetricRegistry register( final String key, final String namespace, final Supplier<Double> supplier ) {
        gauges.put( key, new Gauge( key, namespace, supplier ) );
        return this;
    }

    /**
     * Retrieve a {@link FairMeter} for a given {@code key}.
     * @param key
     * @return FairMeter
     */
    public FairMeter meter( final String key, final String namespace ) {
        return meters.computeIfAbsent( namespace + "." + key, k -> new FairMeter( key, namespace ) );
    }

    /**
     * Take a snapshot of the current state of all registered metrics.
     *
     * @return Snapshot
     */
    public Snapshot takeSnapshot() {
        return new Snapshot(
            convert( meters.values(), MetricRegistry::extractReadOnlyData ),
            convert( gauges.values(), MetricRegistry::extractReadOnlyData )
        );
    }

    private <V, T> List<T> convert( final Iterable<V> values, final Function<V, T> converter ) {
        final List<T> newValues = new ArrayList<>();
        for ( final V value : values )
            newValues.add(converter.apply(value));
        return newValues;
    }

    private static <T extends Metric> T extractReadOnlyData( final ReadOnlyData<T> data ) {
        return data.getData();
    }

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static class Snapshot {
        final Iterable<FairMeter.MeterData> meters;
        final Iterable<Gauge.GaugeData> gauges;
    }
}
