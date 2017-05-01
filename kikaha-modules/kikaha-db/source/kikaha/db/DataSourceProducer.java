package kikaha.db;

import java.sql.SQLException;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.*;
import javax.sql.DataSource;
import kikaha.config.Config;
import kikaha.core.cdi.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class DataSourceProducer {

	@Getter(lazy = true)
	private final Map<String, DataSource> cachedDataSources = startAndCacheConfiguredDataSources();

	@Inject CDI cdi;
	@Inject Config kikahaConf;
	DataSourceFactory defaultDataSourceFactory;

	@PostConstruct
	public void initializePool() throws SQLException {
		final Class<?> clazz = kikahaConf.getClass("server.db.datasource-factory");
		defaultDataSourceFactory = (DataSourceFactory)cdi.load( clazz );
	}

	private Map<String, DataSource> startAndCacheConfiguredDataSources() {
		final Map<String, DataSource> cachedDatasources = new HashMap<>();
		final Config dataSources = readDataSources();
		for ( String confKey : dataSources.getKeys() ) {
			final Config config = dataSources.getConfig(confKey);
			final DataSource dataSource = defaultDataSourceFactory.newDataSource(confKey, config);
			cachedDatasources.put( confKey, dataSource );
		}
		return cachedDatasources;
	}

	Config readDataSources(){
		Config config = kikahaConf.getConfig("server.datasources");
		if ( config == null )
			config = kikahaConf.getConfig("server.db.datasources");
		else {
			log.warn( "Found deprecated 'server.datasources' configuration." );
			log.warn( "Consider use 'server.db.datasources' instead." );
		}
		return config;
	}

	@Produces
	public DataSource produceDataSource( ProviderContext context ) {
		String name = "default";
		final Named source = context.getAnnotation( Named.class );
		if ( source != null )
			name = source.value();
		return produceViburDataSource( name );
	}

	public DataSource produceViburDataSource( String name ) {
		return getCachedDataSources().get( name );
	}
}
