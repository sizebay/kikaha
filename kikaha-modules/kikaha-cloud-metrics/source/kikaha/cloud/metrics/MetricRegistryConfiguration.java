package kikaha.cloud.metrics;

import com.codahale.metrics.MetricRegistry;

/**
 * Configures the {@link MetricRegistry}. Developers are encouraged to implement
 * this interface every time he wants to register its own metrics onto the default
 * {@link MetricRegistry}. Unlike the {@link ReporterConfiguration}, which could be
 * only one implementation configured on the classpath, this interface was designed
 * to have more than once concrete implementation - the Cloud Module will dispatch
 * every subclass of this interface found on classpath in sequence.
 */
public interface MetricRegistryConfiguration {

    void configure(MetricRegistry registry);
}
