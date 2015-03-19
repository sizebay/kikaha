package kikaha.hazelcast.config;

import lombok.val;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.EntryListener;

@Singleton( exposedAs = HazelcastConfigurationListener.class )
public class MultiMapStoreConfigurationListener
		implements HazelcastConfigurationListener {

	@Provided
	ServiceProvider provider;

	@Override
	public void configCreated( Config instance ) throws Exception {
		for ( val mapConfig : instance.getMultiMapConfigs().values() ) {
			handleEntryListeners( mapConfig );
		}
	}

	private void handleEntryListeners( MultiMapConfig mapConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		for ( val mapEntryListener : mapConfig.getEntryListenerConfigs() )
			setImplementation( mapEntryListener );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private void setImplementation( final EntryListenerConfig mapEntryListener )
			throws ClassNotFoundException, ServiceProviderException
	{
		val className = mapEntryListener.getClassName();
		if ( className != null ) {
			val clazz = (Class<? extends EntryListener>)Class.forName( className );
			val impl = provider.load( clazz );
			if ( impl != null )
				mapEntryListener.setImplementation( impl );
		}
	}
}
