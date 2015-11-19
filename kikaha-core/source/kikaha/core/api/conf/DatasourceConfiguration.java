package kikaha.core.api.conf;

public interface DatasourceConfiguration {

	String name();

	Integer poolInitialSize();

	Integer poolMaxSize();

	Boolean poolFair();

	Boolean poolEnableConnectionTracking();

	String driverClassName();

	String jdbcUrl();

	String username();

	String password();

	Integer connectionTimeoutInMs();

	Integer loginTimoutInSeconds();

	Integer acquireRetryDelayInMs();

	Integer acquireRetryAttempt();

	Integer connectionIdleLimitInSeconds();

	Integer validateTimeoutInSeconds();

	String testConnectionQuery();

	String initSql();

	Integer logQueryExecutionLongerThanMs();

	Boolean logStacktraceForLongQueryExecution();

	Integer logLargeResultset();

	Boolean logStacktraceForLargeResultset();

	Integer logConnectionLongerThanMs();

	Boolean clearSqlWarnings();

	Boolean resetDefaultsAfterUse();

	Boolean defaultAutoCommit();

	Boolean defaultReadOnly();

	Integer statementCacheMaxSize();

	String poolReducerClass();

	Integer reducerTimeIntervalInSeconds();

	Integer reducerSamples();
}
