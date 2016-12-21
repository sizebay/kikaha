package kikaha.cloud.metrics;

/**
 * Scheduled Reporters are responsible to report (or store) to an external data store
 * every {@link MetricRegistry.Snapshot} received from the {@link MetricRegistry}.
 */
public interface ScheduledReporter {

    /**
     * Report the {@code snapshot} to an external data store.
     *
     * @param snapshot
     */
    void report(MetricRegistry.Snapshot snapshot);
}
