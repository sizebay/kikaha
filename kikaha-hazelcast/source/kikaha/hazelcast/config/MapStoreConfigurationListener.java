package kikaha.hazelcast.config;

import java.util.List;

import lombok.val;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.EntryListener;

@Singleton( exposedAs = HazelcastConfigurationListener.class )
public class MapStoreConfigurationListener
		implements HazelcastConfigurationListener {

	@Provided
	ServiceProvider provider;

	@Override
	public void configCreated( Config instance ) throws Exception {
		for ( val mapConfig : instance.getMapConfigs().values() ) {
			handleEntryListeners( mapConfig );
			handleStore( mapConfig );
		}
	}

	private void handleEntryListeners( MapConfig mapConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		final List<EntryListenerConfig> configs = mapConfig.getEntryListenerConfigs();
		for ( val mapEntryListener : configs )
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

	private void handleStore( MapConfig mapConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		val mapStoreConfig = mapConfig.getMapStoreConfig();
		if ( mapStoreConfig != null ) {
			setImplementation( mapStoreConfig );
			setFactoryImplementation( mapStoreConfig );
		}
	}

	private void setImplementation( final MapStoreConfig mapStoreConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		val className = mapStoreConfig.getClassName();
		if ( className != null ) {
			val clazz = Class.forName( className );
			val impl = provider.load( clazz );
			if ( impl != null )
				mapStoreConfig.setImplementation( impl );
		}
	}

	private void setFactoryImplementation( final MapStoreConfig mapStoreConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		val factoryClassName = mapStoreConfig.getFactoryClassName();
		if ( factoryClassName != null ) {
			val clazz = Class.forName( factoryClassName );
			val impl = provider.load( clazz );
			if ( impl != null )
				mapStoreConfig.setFactoryImplementation( impl );
		}
	}
}
