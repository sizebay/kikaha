package kikaha.cloud.metrics;

import com.codahale.metrics.Reporter;

/**
 * Creates a {@link Reporter}.
 */
public interface ReporterFactory {

    Reporter create();
}
