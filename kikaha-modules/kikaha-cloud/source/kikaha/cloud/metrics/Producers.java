package kikaha.cloud.metrics;

import com.codahale.metrics.MetricRegistry;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

/**
 * Singleton objects that will be available on the default CDI context.
 */
@Singleton
public class Producers {

    final MetricRegistry metricRegistry = new MetricRegistry();

    @Produces
    public MetricRegistry produceMetricRegistry(){
        return metricRegistry;
    }
}
