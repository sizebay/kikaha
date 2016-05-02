package kikaha.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;

import kikaha.config.Config;
import kikaha.core.cdi.ProviderContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.vibur.dbcp.ViburDBCPDataSource;

@Slf4j
@Singleton
public class ViburDatasourceProducer {

	@Getter(lazy = true)
	private final Map<String, ViburDBCPDataSource> cachedDataSources = startAndCacheConfiguredDatasources();

	@Inject
	Config kikahaConf;

	@PostConstruct
	public void initializePool() throws SQLException {
		Runtime.getRuntime().addShutdownHook( new Thread( this::stopDataSources ) );
	}

	private Map<String, ViburDBCPDataSource> startAndCacheConfiguredDatasources() {
		final Map<String, ViburDBCPDataSource> cachedDatasources = new HashMap<>();
		final Config dataSources = kikahaConf.getConfig("server.datasources");
		for ( String confKey : dataSources.getKeys() ) {
			final DataSourceConfiguration dsConf = DataSourceConfiguration.from( confKey, dataSources.getConfig(confKey) );
			final ViburDBCPDataSource ds = createDatasource( dsConf );
			ds.start();
			cachedDatasources.put( confKey, ds );
		}
		return cachedDatasources;
	}

	private ViburDBCPDataSource createDatasource( final DataSourceConfiguration dsConf ) {
		log.info( "Starting DataSource " + dsConf.name() + "...." );
		final ViburDBCPDataSource ds = new ViburDBCPDataSource();
		ds.setAcquireRetryAttempts( dsConf.acquireRetryAttempt() );
		ds.setAcquireRetryDelayInMs( dsConf.acquireRetryDelayInMs() );
		ds.setClearSQLWarnings( dsConf.clearSqlWarnings() );
		ds.setConnectionIdleLimitInSeconds( dsConf.connectionIdleLimitInSeconds() );
		ds.setConnectionTimeoutInMs( dsConf.connectionTimeoutInMs() );
		ds.setDefaultAutoCommit( dsConf.defaultAutoCommit() );
		ds.setDefaultReadOnly( dsConf.defaultReadOnly() );
		if ( dsConf.driverClassName() != null )
			ds.setDriverClassName( dsConf.driverClassName() );
		ds.setInitSQL( dsConf.initSql() );
		ds.setJdbcUrl( dsConf.jdbcUrl() );
		ds.setLogConnectionLongerThanMs( dsConf.logConnectionLongerThanMs() );
		ds.setLoginTimeoutInSeconds( dsConf.loginTimeoutInSeconds() );
		ds.setLogLargeResultSet( dsConf.logLargeResultset() );
		ds.setLogQueryExecutionLongerThanMs( dsConf.logQueryExecutionLongerThanMs() );
		ds.setLogStackTraceForLargeResultSet( dsConf.logStacktraceForLargeResultset() );
		ds.setLogStackTraceForLongQueryExecution( dsConf.logStacktraceForLongQueryExecution() );
		ds.setPoolFair( dsConf.poolFair() );
		ds.setPoolInitialSize( dsConf.poolInitialSize() );
		ds.setPoolMaxSize( dsConf.poolMaxSize() );
		ds.setUsername( dsConf.username() );
		ds.setPassword( dsConf.password() );
		log.debug( "Connection: jdbc-url: " + ds.getJdbcUrl() );
		log.debug( "Connection: username: " + ds.getUsername() );
		return ds;
	}

	public void stopDataSources() {
		log.info("Stopping DataSources...");
		getCachedDataSources().values().forEach(ViburDBCPDataSource::terminate);
	}

	@Produces
	public DataSource produceDataSource( ProviderContext context ) {
		return produceViburDataSource( context );
	}

	@Produces
	public ViburDBCPDataSource produceViburDataSource( ProviderContext context ) {
		String name = "default";
		final Named source = context.getAnnotation( Named.class );
		if ( source != null )
			name = source.value();
		return getCachedDataSources().get( name );
	}
}
