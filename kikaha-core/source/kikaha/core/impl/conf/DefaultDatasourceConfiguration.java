package kikaha.core.impl.conf;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException.Missing;

import kikaha.core.api.conf.DatasourceConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class DefaultDatasourceConfiguration implements DatasourceConfiguration {

	final String name;
	final Integer poolInitialSize;
	final Integer poolMaxSize;
	final Boolean poolFair;
	final Boolean poolEnableConnectionTracking;
	final String driverClassName;
	final String jdbcUrl;
	final String username;
	final String password;
	final Integer connectionTimeoutInMs;
	final Integer loginTimoutInSeconds;
	final Integer acquireRetryDelayInMs;
	final Integer acquireRetryAttempt;
	final Integer connectionIdleLimitInSeconds;
	final Integer validateTimeoutInSeconds;
	final String testConnectionQuery;
	final String initSql;
	final Integer logQueryExecutionLongerThanMs;
	final Boolean logStacktraceForLongQueryExecution;
	final Integer logLargeResultset;
	final Boolean logStacktraceForLargeResultset;
	final Integer logConnectionLongerThanMs;
	final Boolean clearSqlWarnings;
	final Boolean resetDefaultsAfterUse;
	final Boolean defaultAutoCommit;
	final Boolean defaultReadOnly;
	final Integer statementCacheMaxSize;
	final String poolReducerClass;
	final Integer reducerTimeIntervalInSeconds;
	final Integer reducerSamples;

	public static DatasourceConfiguration from( String name, Config config ) {
		try {
			return new DefaultDatasourceConfiguration( name,
					config.getInt( "pool-initial-size" ),
					config.getInt( "pool-max-size" ),
					config.getBoolean( "pool-fair" ),
					config.getBoolean( "pool-enable-connection-tracking" ),
					config.getString( "driver-class-name" ),
					config.getString( "jdbc-url" ),
					config.getString( "username" ),
					config.getString( "password" ),
					config.getInt( "connection-timeout-in-ms" ),
					config.getInt( "login-timout-in-seconds" ),
					config.getInt( "acquire-retry-delay-in-ms" ),
					config.getInt( "acquire-retry-attempt" ),
					config.getInt( "connection-idle-limit-in-seconds" ),
					config.getInt( "validate-timeout-in-seconds" ),
					config.getString( "test-connection-query" ),
					config.getString( "init-sql" ),
					config.getInt( "log-query-execution-longer-than-ms" ),
					config.getBoolean( "log-stacktrace-for-long-query-execution" ),
					config.getInt( "log-large-resultset" ),
					config.getBoolean( "log-stacktrace-for-large-resultset" ),
					config.getInt( "log-connection-longer-than-ms" ),
					config.getBoolean( "clear-sql-warnings" ),
					config.getBoolean( "reset-defaults-after-use" ),
					config.getBoolean( "default-auto-commit" ),
					config.getBoolean( "default-read-only" ),
					config.getInt( "statement-cache-max-size" ),
					config.getString( "pool-reducer-class" ),
					config.getInt( "reducer-time-interval-in-seconds" ),
					config.getInt( "reducer-samples" ) );
		} catch ( final Missing cause ) {
			log.debug( "Can't instantiate '" + name + "' datasource.", cause );
			return null;
		}
	}
}
