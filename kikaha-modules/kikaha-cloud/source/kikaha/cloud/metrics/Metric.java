package kikaha.cloud.metrics;

/**
 * A metric that could be reported.
 */
public interface Metric {

    String getName();
    String getNamespace();
}
