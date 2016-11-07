package kikaha.uworkers.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.*;
import kikaha.config.Config;
import kikaha.uworkers.core.*;

/**
 * A {@link EndpointFactory} implementation that creates {@link EndpointInboxSupplier}
 * to handle tasks within the same JVM.
 */
@Singleton
public class LocalEndpointFactory implements EndpointFactory {

	final Map<String, LocalEndpointInboxSupplier> cachedSuppliers = new ConcurrentHashMap<>();

	@Inject EndpointContext endpointContext;

	@Override
	public LocalEndpointInboxSupplier createSupplier( String endpointName) {
		return cachedSuppliers.computeIfAbsent( endpointName, this::instantiateSupplier );
	}

	LocalEndpointInboxSupplier instantiateSupplier( String endpointName ) {
		final Config endpointConfig = endpointContext.getEndpointConfig(endpointName);
		final int poolSize = endpointConfig.getInteger("pool-size", -1);
		return ( poolSize > 0 )
			? LocalEndpointInboxSupplier.withFixedSize( poolSize )
			: LocalEndpointInboxSupplier.withElasticSize();
	}

	@Override
	public LocalWorkerRef createWorkerRef( String endpointName ) {
		final LocalEndpointInboxSupplier supplier = createSupplier(endpointName);
		return new LocalWorkerRef( supplier );
	}

	/**
	 * This factory should have a lower priority to avoid conflicts with other
	 * factories.
	 * @return {@link Integer#MIN_VALUE}
	 */
	@Override
	public int priority() { return Integer.MIN_VALUE; }

	@Override
	public boolean canHandleEndpoint(String endpointName) { return true; }
}

