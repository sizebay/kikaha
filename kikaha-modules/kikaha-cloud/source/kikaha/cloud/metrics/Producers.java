package kikaha.cloud.metrics;

import com.codahale.metrics.MetricRegistry;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

/**
 * Singleton objects that will be available on the default CDI context.
 */
@Singleton
public class Producers {

    final MetricRegistry metricRegistry = new MetricRegistry();
    final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    @Produces
    public MetricRegistry produceMetricRegistry(){
        return metricRegistry;
    }

    @Produces
    public MBeanServer produceMBeanServer(){
        return  mBeanServer;
    }
}
