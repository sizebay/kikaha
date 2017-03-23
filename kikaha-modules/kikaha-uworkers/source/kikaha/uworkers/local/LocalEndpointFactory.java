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

	@Override
	public LocalEndpointInboxSupplier createSupplier( EndpointConfig config ) {
		return cachedSuppliers.computeIfAbsent( config.getEndpointName(), n -> instantiateSupplier( config ) );
	}

	LocalEndpointInboxSupplier instantiateSupplier( EndpointConfig config ) {
		final int poolSize = config.getConfig().getInteger("pool-size", -1);
		return ( poolSize > 0 )
			? LocalEndpointInboxSupplier.withFixedSize( poolSize )
			: LocalEndpointInboxSupplier.withElasticSize();
	}

	@Override
	public LocalWorkerRef createWorkerRef( EndpointConfig config ) {
		final LocalEndpointInboxSupplier supplier = createSupplier( config );
		return new LocalWorkerRef( supplier );
	}
}
