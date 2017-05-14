package kikaha.cloud.metrics;

import java.lang.management.ManagementFactory;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.*;
import javax.inject.*;
import javax.management.MBeanServer;
import com.codahale.metrics.*;
import com.codahale.metrics.health.HealthCheckRegistry;
import kikaha.config.Config;
import kikaha.core.cdi.CDI;

/**
 * Singleton objects that will be available on the default CDI context.
 */
@Singleton
public class Producers {

    static final String
        IS_MODULE_ENABLED = "server.metrics.enabled",
        SHOULD_STORE_INDIVIDUAL_WEB_METRICS = "server.metrics.web-transactions.store-individual-metrics",
        SHOULD_STORE_SUMMARIZED_WEB_METRICS = "server.metrics.web-transactions.store-summarized-metrics",
        REPORTER_CONFIGURATION_CLASS = "server.metrics.reporter-configuration",
        REPORTER_LISTENER_CLASS = "server.metrics.reporter-metric-listener",
        REPORTER_FILTER_CLASS = "server.metrics.reporter-metric-filter";

    final MetricRegistry metricRegistry = new MetricRegistry();
    final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
    final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    @Inject CDI cdi;
    @Inject Config config;

    MetricConfiguration metricConfiguration;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void loadConfig(){
        metricConfiguration = new MetricConfiguration(
            (Class<? extends ReporterConfiguration>) config.getClass( REPORTER_CONFIGURATION_CLASS ),
            (Class<? extends MetricRegistryListener>) config.getClass( REPORTER_LISTENER_CLASS ),
            (Class<? extends MetricFilter>) config.getClass( REPORTER_FILTER_CLASS ),
            config.getBoolean( SHOULD_STORE_INDIVIDUAL_WEB_METRICS ),
            config.getBoolean( SHOULD_STORE_SUMMARIZED_WEB_METRICS ),
            config.getBoolean( IS_MODULE_ENABLED ),
            cdi
        );
    }

    @Produces MetricRegistry produceMetricRegistry(){
        return metricRegistry;
    }

    @Produces MBeanServer produceMBeanServer(){
        return  mBeanServer;
    }

    @Produces HealthCheckRegistry produceHealthCheckRegistry(){
        return healthCheckRegistry;
    }

    @Produces MetricConfiguration produceConfiguration(){
        return metricConfiguration;
    }
}
