package kikaha.db;

import kikaha.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class ViburDataSourceConfiguration {

	final String name;
	final int poolInitialSize;
	final int poolMaxSize;
	final boolean poolFair;
	final boolean poolEnableConnectionTracking;
	final String driverClassName;
	final String jdbcUrl;
	final String username;
	final String password;
	final int connectionTimeoutInMs;
	final int loginTimeoutInSeconds;
	final int acquireRetryDelayInMs;
	final int acquireRetryAttempt;
	final int connectionIdleLimitInSeconds;
	final int validateTimeoutInSeconds;
	final String testConnectionQuery;
	final String initSql;
	final int logQueryExecutionLongerThanMs;
	final boolean logStacktraceForLongQueryExecution;
	final int logLargeResultset;
	final boolean logStacktraceForLargeResultset;
	final int logConnectionLongerThanMs;
	final boolean clearSqlWarnings;
	final boolean resetDefaultsAfterUse;
	final boolean defaultAutoCommit;
	final boolean defaultReadOnly;
	final int statementCacheMaxSize;
	final String poolReducerClass;
	final int reducerTimeIntervalInSeconds;
	final int reducerSamples;

	@Override
	public String toString() {
		return name + "[" + jdbcUrl + ";" + username + ";" + password + ";" + driverClassName + "]";
	}

	public static ViburDataSourceConfiguration from(String name, Config config ) {
		return new ViburDataSourceConfiguration( name,
				config.getInteger( "pool-initial-size", 10 ),
				config.getInteger( "pool-max-size", 100 ),
				config.getBoolean( "pool-fair", true ),
				config.getBoolean( "pool-enable-connection-tracking", false ),
				config.getString( "driver-class-name" ),
				config.getString( "jdbc-url" ),
				config.getString( "username" ),
				config.getString( "password" ),
				config.getInteger( "connection-timeout-in-ms", 30000 ),
				config.getInteger( "login-timeout-in-seconds", 10 ),
				config.getInteger( "acquire-retry-delay-in-ms", 1000 ),
				config.getInteger( "acquire-retry-attempt", 3 ),
				config.getInteger( "connection-idle-limit-in-seconds", 15 ),
				config.getInteger( "validate-timeout-in-seconds", 3 ),
				config.getString( "test-connection-query", "isValid" ),
				config.getString( "init-sql" ),
				config.getInteger( "log-query-execution-longer-than-ms", 3000 ),
				config.getBoolean( "log-stacktrace-for-long-query-execution", false ),
				config.getInteger( "log-large-resultset", 500 ),
				config.getBoolean( "log-stacktrace-for-large-resultset", false ),
				config.getInteger( "log-connection-longer-than-ms", 3000 ),
				config.getBoolean( "clear-sql-warnings", false ),
				config.getBoolean( "reset-defaults-after-use", false ),
				config.getBoolean( "default-auto-commit", true ),
				config.getBoolean( "default-read-only", false ),
				config.getInteger( "statement-cache-max-size", 0 ),
				config.getString( "pool-reducer-class", "org.vibur.dbcp.pool.PoolReducer" ),
				config.getInteger( "reducer-time-interval-in-seconds", 60 ),
				config.getInteger( "reducer-samples", 20 ) );
	}
}