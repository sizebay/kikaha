package kikaha.hazelcast;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

import com.hazelcast.core.ManagedContext;

@Singleton
public class HazelcastTripManagedContext implements ManagedContext {

	@Provided
	ServiceProvider provider;

	@Override
	public Object initialize( Object obj ) {
		try {
			if ( obj.getClass().isAnnotationPresent( Singleton.class ) )
				provider.provideOn( obj );
			return obj;
		} catch ( ServiceProviderException e ) {
			throw new RuntimeException( e );
		}
	}
}
