package kikaha.cloud.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reporter;

/**
 * Creates a {@link Reporter}.
 */
public interface ReporterConfiguration {

    /**
     * Notifies the {@link ReporterConfiguration} that the {@link MetricRegistry} is
     * configured and ready to be reported. Developers are encouraged to instantiate
     * a Reporter and start is reporting process in background.
     *
     * @param registry
     */
    void configureAndStartReportFor(MetricRegistry registry);
}
