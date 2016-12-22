package kikaha.cloud.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * A {@link ReporterConfiguration} implementation that starts a {@link JmxReporter}
 * in background.
 */
@Slf4j
public class JMXReporterConfiguration implements ReporterConfiguration {

    @Override
    public void configureAndStartReportFor(MetricRegistry registry) {
        final JmxReporter reporter = JmxReporter.forRegistry(registry).build();
        reporter.start();
        log.info( "JVM Metric Reporter started." );
    }
}
