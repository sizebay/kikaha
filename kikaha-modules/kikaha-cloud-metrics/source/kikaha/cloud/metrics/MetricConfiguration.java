package kikaha.cloud.metrics;

import com.codahale.metrics.*;
import kikaha.core.cdi.CDI;
import lombok.*;
import lombok.experimental.Accessors;

/**
 *
 */
@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class MetricConfiguration {

	final Class<? extends ReporterConfiguration> reporterConfigurationClass;
	final Class<? extends MetricRegistryListener> registryListenerClass;
	final Class<? extends MetricFilter> metricFilterClass;

	final boolean
		shouldStoreIndividualWebMetrics,
		shouldStoreSummarizedWebMetrics,
		isEnabled;

	final CDI cdi;

	public ReporterConfiguration reporterConfiguration(){
		return cdi.load( reporterConfigurationClass );
	}

	public MetricRegistryListener registryListener(){
		return cdi.load( registryListenerClass );
	}

	public MetricFilter metricFilter(){
		return cdi.load( metricFilterClass );
	}
}
