package kikaha.cloud.metrics;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import javax.inject.*;
import javax.management.MBeanServer;
import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jvm.*;
import io.undertow.Undertow;
import io.undertow.server.*;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * The Cloud Metric {@link Module}.
 */
@Slf4j
@Singleton
public class MetricsModule implements HttpHandlerDeploymentModule.HttpHandlerDeploymentCustomizer, Module {

    final static String
        NAMESPACE_WEB = "kikaha.transactions", NAMESPACE_JVM = "kikaha.jvm",
        SUMMARIZED = "summarized",
        IS_MODULE_ENABLED = "server.metrics.enabled",
        SHOULD_STORE_INDIVIDUAL_WEB_METRICS = "server.metrics.web-transactions.store-individual-metrics",
        SHOULD_STORE_SUMMARIZED_WEB_METRICS = "server.metrics.web-transactions.store-summarized-metrics",
        JVM_METRICS = "server.metrics.jvm",
        REPORTER_CONFIGURATION_CLASS = "server.metrics.reporter-configuration"
    ;

    @Getter final String name = "metrics";
    final Map<String, Consumer<String>> jvmMetrics = new HashMap<>();

    @Inject MetricRegistry metricRegistry;
    @Inject MBeanServer mBeanServer;
    @Inject Config config;
    @Inject @Typed( MetricRegistryConfiguration.class )
    Iterable<MetricRegistryConfiguration> metricConfigurations;

    Class<? extends ReporterConfiguration> reporterConfigurationClass;
    boolean
        isEnabled = false,
        shouldStoreIndividualWebMetrics = false,
        shouldStoreSummarizedWebMetrics = false
    ;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void loadConfig(){
        isEnabled = config.getBoolean( IS_MODULE_ENABLED );
        shouldStoreIndividualWebMetrics = config.getBoolean( SHOULD_STORE_INDIVIDUAL_WEB_METRICS );
        shouldStoreSummarizedWebMetrics = config.getBoolean( SHOULD_STORE_SUMMARIZED_WEB_METRICS );
        reporterConfigurationClass = (Class<? extends ReporterConfiguration>) config.getClass( REPORTER_CONFIGURATION_CLASS );
        registerAvailableJvmMetrics();
    }

    private void registerAvailableJvmMetrics(){
        jvmMetrics.put( "memory-usage", k -> registerJvmMetrics( "memory", new MemoryUsageGaugeSet()) );
        jvmMetrics.put( "buffer-pool-usage", k -> registerJvmMetrics( "buffer-pool", new BufferPoolMetricSet( mBeanServer )) );
        jvmMetrics.put( "thread-usage", k -> registerJvmMetrics( "threads", new ThreadStatesGaugeSet() ));
        jvmMetrics.put( "gc-usage", k -> registerJvmMetrics( "gc", new GarbageCollectorMetricSet() ) );
        jvmMetrics.put( "fd-usage", k -> registerJvmMetric( "fd", new FileDescriptorRatioGauge() ) );
    }

    @Override
    public HttpHandler customize( HttpHandler httpHandler, final WebResource webResource) {
        if ( !isEnabled) return httpHandler;
        log.debug( "Extracting metrics for " + httpHandler.toString() + " ..." );

        final String name = webResource.method() + " " + webResource.path();

        if (shouldStoreIndividualWebMetrics) {
            final Timer meter = metricRegistry.timer( MetricRegistry.name(NAMESPACE_WEB, name) );
            httpHandler = new MetricHttpHandler( httpHandler, meter );
            log.debug( "  Registered individual metric for " + name );
        }

        if ( shouldStoreSummarizedWebMetrics ) {
            final Timer meter = metricRegistry.timer( MetricRegistry.name(NAMESPACE_WEB, SUMMARIZED) );
            httpHandler = new MetricHttpHandler( httpHandler, meter );
            log.debug( "  Registered summarized metric for " + name );
        }

        return httpHandler;
    }

    @Override
    public void load( final Undertow.Builder server, final DeploymentContext context ) throws IOException {
        if ( !isEnabled ) return;
        log.info( "Initializing the Cloud Metric module..." );
        runExternalMetricConfigurations();
        loadJvmMetrics();

        final ReporterConfiguration reporterConfiguration = initializeReporter();
        reporterConfiguration.configureAndStartReportFor( metricRegistry );
    }

    private void runExternalMetricConfigurations() {
        for ( final MetricRegistryConfiguration configuration : metricConfigurations )
            configuration.configure( metricRegistry );
    }

    void loadJvmMetrics(){
        final Config config = this.config.getConfig(JVM_METRICS);
        for (String key : config.getKeys()) {
            if ( config.getBoolean( key ) )
                jvmMetrics.get( key ).accept( key );
        }
    }

    void registerJvmMetrics(String namespace, MetricSet metricSet){
        final Map<String, Metric> metrics = metricSet.getMetrics();
        for ( final String key : metrics.keySet() ) {
            final Metric metric = metrics.get(key);
            registerJvmMetric( MetricRegistry.name(namespace, key), metric );
        }
    }

    void registerJvmMetric(String key, Metric metric ){
        final String metricName = MetricRegistry.name(NAMESPACE_JVM, key);
        metricRegistry.register( metricName, metric );
        log.debug( "  Registered JVM metric " + metricName );
    }

    ReporterConfiguration initializeReporter() {
        try {
            return this.reporterConfigurationClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new UnsupportedOperationException( e );
        }
    }
}

@RequiredArgsConstructor
class MetricHttpHandler implements HttpHandler {

    final HttpHandler httpHandler;
    final Timer timer;

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        final Timer.Context context = timer.time();
        try {
            httpHandler.handleRequest(exchange);
        } finally {
            context.stop();
        }
    }
}