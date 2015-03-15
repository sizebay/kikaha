package kikaha.hazelcast.config;

import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapConfig.EvictionPolicy;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MaxSizeConfig.MaxSizePolicy;
import com.hazelcast.config.NearCacheConfig;

@DelegateTo( MapConfig.class )
public interface MapConfiguration {

	@ConfigItem( "name" )
	void setName( String value );

	@ConfigItem( "in-memory-format" )
	void setInMemoryFormat( InMemoryFormat value );

	@ConfigItem( "backup-count" )
	void setBackupCount( int value );

	@ConfigItem( "async-backup-count" )
	void setAsyncBackupCount( int value );

	@ConfigItem( "read-backup-data" )
	void setReadBackupData( boolean value );

	@ConfigItem( "time-to-live-seconds" )
	void setTimeToLiveSeconds( int value );

	@ConfigItem( "max-idle-seconds" )
	void setMaxIdleSeconds( int value );

	@ConfigItem( "eviction-policy" )
	void setEvictionPolicy( EvictionPolicy value );

	@ConfigItem( "max-size" )
	void setMaxSizeConfig( MaxSize value );

	@DelegateTo( MaxSizeConfig.class )
	public interface MaxSize {

		@ConfigItem( "policy" )
		void setMaxSizePolicy( MaxSizePolicy maxSizePolicy );

		@ConfigItem( "value" )
		void setSize( int size );
	}

	@ConfigItem( "eviction-percentage" )
	void setEvictionPercentage( int value );

	@ConfigItem( "near-cache" )
	void setNearCacheConfig( NearCache value );

	@DelegateTo( NearCacheConfig.class )
	public interface NearCache {

		@ConfigItem( "invalidate-on-change" )
		void setInvalidateOnChange( boolean value );
	}

	@ConfigItem( "map-store" )
	void setMapStoreConfig( MapStore value );

	@DelegateTo( MapStoreConfig.class )
	public interface MapStore {

		@ConfigItem( "enabled" )
		void setEnabled( boolean value );

		@ConfigItem( "write-delay-seconds" )
		void setWriteDelaySeconds( int value );

		@ClassInstance
		@ConfigItem( "class-name" )
		void setImplementation( Object value );
	}

	@ConfigItem( "indexes" )
	@CallMethodForEachFoundEntry
	void addMapIndexConfig( MapIndex value );

	@DelegateTo( MapIndexConfig.class )
	public interface MapIndex {

		@ConfigItem( "attribute" )
		void setAttribute( String attribute );

		@ConfigItem( "ordered" )
		void setOrdered( boolean value );
	}

	@ConfigItem( "entry-listeners" )
	@CallMethodForEachFoundEntry
	void addEntryListenerConfig( EntryListener listenerConfig );

	@DelegateTo( EntryListenerConfig.class )
	public interface EntryListener {

		@ClassInstance
		@ConfigItem( "class-name" )
		@SuppressWarnings( "rawtypes" )
		void setImplementation( com.hazelcast.core.EntryListener value );

		@ConfigItem( "local" )
		void setLocal( boolean value );

		@ConfigItem( "include-value" )
		void setIncludeValue( boolean value );
	}
}