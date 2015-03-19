package kikaha.hazelcast.config;

import lombok.val;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;

import com.hazelcast.config.Config;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.ItemListener;
import com.hazelcast.core.QueueStore;
import com.hazelcast.core.QueueStoreFactory;

@Singleton( exposedAs = HazelcastConfigurationListener.class )
public class QueueStoreConfigurationListener
		implements HazelcastConfigurationListener {

	@Provided
	ServiceProvider provider;

	@Override
	public void configCreated( Config instance ) throws Exception {
		for ( val queueConfig : instance.getQueueConfigs().values() ) {
			handleStore( queueConfig );
			handleEntryListeners( queueConfig );
		}
	}

	private void handleEntryListeners( QueueConfig mapConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		for ( val mapEntryListener : mapConfig.getItemListenerConfigs() )
			setImplementation( mapEntryListener );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private void setImplementation( final ItemListenerConfig mapEntryListener )
			throws ClassNotFoundException, ServiceProviderException
	{
		val className = mapEntryListener.getClassName();
		if ( className != null ) {
			val clazz = (Class<? extends ItemListener>)Class.forName( className );
			val impl = provider.load( clazz );
			if ( impl != null )
				mapEntryListener.setImplementation( impl );
		}
	}

	private void handleStore( QueueConfig queueConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		val mapStoreConfig = queueConfig.getQueueStoreConfig();
		if ( mapStoreConfig != null ) {
			setImplementation( mapStoreConfig );
			setFactoryImplementation( mapStoreConfig );
		}
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private void setImplementation( final QueueStoreConfig mapStoreConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		val className = mapStoreConfig.getClassName();
		if ( className != null ) {
			val clazz = (Class<? extends QueueStore>)Class.forName( className );
			val impl = provider.load( clazz );
			if ( impl != null )
				mapStoreConfig.setStoreImplementation( impl );
		}
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private void setFactoryImplementation( final QueueStoreConfig mapStoreConfig )
			throws ClassNotFoundException, ServiceProviderException
	{
		val factoryClassName = mapStoreConfig.getFactoryClassName();
		if ( factoryClassName != null ) {
			val clazz = (Class<? extends QueueStoreFactory>)Class.forName( factoryClassName );
			val impl = provider.load( clazz );
			if ( impl != null )
				mapStoreConfig.setFactoryImplementation( impl );
		}
	}
}
