package kikaha.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import kikaha.core.api.Source;
import kikaha.core.api.conf.Configuration;
import kikaha.core.api.conf.DatasourceConfiguration;
import lombok.extern.slf4j.Slf4j;

import org.vibur.dbcp.ViburDBCPDataSource;

import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.ProviderContext;
import trip.spi.Singleton;

@Slf4j
@Singleton
public class ViburDatasourceProducer {

	final Map<String, ViburDBCPDataSource> cachedDatasources = new HashMap<>();

	@Provided
	Configuration kikahaConf;

	@PostConstruct
	public void startAndCacheConfiguredDatasources() throws SQLException {
		for ( final DatasourceConfiguration dsConf : kikahaConf.datasources().values() ) {
			final ViburDBCPDataSource ds = createDatasource( dsConf );
			ds.start();
			log.info( "Datasource " + dsConf.name() + " started." );
			log.debug( ds.toString() );
			cachedDatasources.put( dsConf.name(), ds );
		}
	}

	private ViburDBCPDataSource createDatasource( final DatasourceConfiguration dsConf ) {
		final ViburDBCPDataSource ds = new ViburDBCPDataSource();
		ds.setAcquireRetryAttempts( dsConf.acquireRetryAttempt() );
		ds.setAcquireRetryDelayInMs( dsConf.acquireRetryDelayInMs() );
		ds.setClearSQLWarnings( dsConf.clearSqlWarnings() );
		ds.setConnectionIdleLimitInSeconds( dsConf.connectionIdleLimitInSeconds() );
		ds.setConnectionTimeoutInMs( dsConf.connectionTimeoutInMs() );
		ds.setDefaultAutoCommit( dsConf.defaultAutoCommit() );
		ds.setDefaultReadOnly( dsConf.defaultReadOnly() );
		ds.setDriverClassName( dsConf.driverClassName() );
		ds.setInitSQL( dsConf.initSql() );
		ds.setJdbcUrl( dsConf.jdbcUrl() );
		ds.setLogConnectionLongerThanMs( dsConf.logConnectionLongerThanMs() );
		ds.setLoginTimeoutInSeconds( dsConf.loginTimoutInSeconds() );
		ds.setLogLargeResultSet( dsConf.logLargeResultset() );
		ds.setLogQueryExecutionLongerThanMs( dsConf.logQueryExecutionLongerThanMs() );
		ds.setLogStackTraceForLargeResultSet( dsConf.logStacktraceForLargeResultset() );
		ds.setLogStackTraceForLongQueryExecution( dsConf.logStacktraceForLongQueryExecution() );
		return ds;
	}

	@Producer
	public DataSource produceDataSource( ProviderContext context ) {
		return produceViburDataSource( context );
	}

	@Producer
	public ViburDBCPDataSource produceViburDataSource( ProviderContext context ) {
		String name = "default";
		final Source source = context.getAnnotation( Source.class );
		if ( source != null )
			name = source.value();
		return cachedDatasources.get( name );
	}
}
