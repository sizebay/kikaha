package kikaha.db;

import javax.sql.DataSource;
import kikaha.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.vibur.dbcp.ViburDBCPDataSource;

/**
 *
 */
@Slf4j
public class ViburDataSourceFactory implements DataSourceFactory {

	@Override
	public DataSource newDataSource(String name, Config config) {
		final DataSourceConfiguration viburConf = DataSourceConfiguration.from(name, config);
		log.debug( "Starting Vibur DataSource " + viburConf );
		final ViburDBCPDataSource ds = createDatasource( viburConf );
		ds.start();
		Runtime.getRuntime().addShutdownHook( new Thread(ds::terminate) );
		return ds;
	}

	private ViburDBCPDataSource createDatasource(final DataSourceConfiguration dsConf ) {
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
		return ds;
	}
}
