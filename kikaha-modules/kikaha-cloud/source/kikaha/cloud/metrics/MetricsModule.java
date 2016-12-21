package kikaha.cloud.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.http.HttpHandlerDeploymentModule;
import kikaha.core.modules.http.WebResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * Created by miere.teixeira on 21/12/2016.
 */
@Slf4j
@Singleton
public class MetricsModule implements HttpHandlerDeploymentModule.HttpHandlerDeploymentCustomizer, Module {

    final static String
        NAMESPACE_WEB = "kikaha.transactions",
        SUMMARIZED = "summarized",
        IS_MODULE_ENABLED = "server.metrics.enabled",
        SHOULD_STORE_INDIVIDUAL_WEB_METRICS = "server.metrics.store-individual-web-metrics",
        SHOULD_STORE_SUMMARIZED_WEB_METRICS = "server.metrics.store-summarized-web-metrics"
    ;

    @Inject MetricRegistry metricRegistry;
    @Inject Config config;

    boolean
        isEnabled = false,
        shouldStoreIndividualWebMetrics = false,
        shouldStoreSummarizedWebMetrics = false
    ;

    @PostConstruct
    public void loadConfig(){
        isEnabled = config.getBoolean( IS_MODULE_ENABLED );
        shouldStoreIndividualWebMetrics = config.getBoolean( SHOULD_STORE_INDIVIDUAL_WEB_METRICS );
        shouldStoreSummarizedWebMetrics = config.getBoolean(SHOULD_STORE_SUMMARIZED_WEB_METRICS);
    }

    @Override
    public HttpHandler customize( HttpHandler httpHandler, final WebResource webResource) {
        if ( !isEnabled) return httpHandler;

        final String name = webResource.method() + " " + webResource.path();

        if (shouldStoreIndividualWebMetrics) {
            final Timer meter = metricRegistry.timer( MetricRegistry.name(NAMESPACE_WEB, name) );
            httpHandler = new MetricHttpHandler( httpHandler, meter );
            log.debug( "Registered individual metric for " + name );
        }

        if ( shouldStoreSummarizedWebMetrics ) {
            final Timer meter = metricRegistry.timer( MetricRegistry.name(NAMESPACE_WEB, SUMMARIZED) );
            httpHandler = new MetricHttpHandler( httpHandler, meter );
            log.debug( "Registered summarized metric for " + name );
        }

        return httpHandler;
    }

    @Override
    public void load( final Undertow.Builder server, final DeploymentContext context ) throws IOException {
        if ( !isEnabled) return;
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