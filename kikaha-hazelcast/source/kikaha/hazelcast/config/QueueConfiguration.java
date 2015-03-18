package kikaha.hazelcast.config;

import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;

@DelegateTo( QueueConfig.class )
public interface QueueConfiguration {

	@ConfigItem( "name" )
	void setName( String value );

	@ConfigItem( "max-size" )
	void setMaxSize( int value );

	@ConfigItem( "backup-count" )
	void setBackupCount( int value );

	@ConfigItem( "async-backup-count" )
	void setAsyncBackupCount( int value );

	@ConfigItem( "empty-queue-ttl" )
	void setEmptyQueueTtl( int value );

	@ConfigItem( "statistics-enabled" )
	void setStatisticsEnabled( boolean value );

	@ConfigItem( "item-listeners" )
	@CallMethodForEachFoundEntry
	void addItemListenerConfig( ItemListener listenerConfig );

	@DelegateTo( ItemListenerConfig.class )
	public interface ItemListener {

		@ClassInstance
		@ConfigItem( "class-name" )
		@SuppressWarnings( "rawtypes" )
		void setImplementation( com.hazelcast.core.ItemListener value );

		@ConfigItem( "include-value" )
		void setIncludeValue( boolean value );
	}

	@ConfigItem( "queue-store" )
	void setQueueStoreConfig( QueueStore value );

	@DelegateTo( QueueStoreConfig.class )
	public interface QueueStore {

		@ConfigItem( "enabled" )
		void setEnabled( boolean value );

		@ClassInstance
		@ConfigItem( "class-name" )
		@SuppressWarnings( "rawtypes" )
		void setStoreImplementation( com.hazelcast.core.QueueStore value );
	}
}
