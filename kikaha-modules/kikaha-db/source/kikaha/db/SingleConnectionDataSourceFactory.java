package kikaha.db;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;
import kikaha.config.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A {@link DataSourceFactory} that creates a simple DataSource with no
 * connection pool at all. Useful for single threaded environments which
 * is not recommended to open lots of connection up-front like AWS Lambda.
 */
@Slf4j
public class SingleConnectionDataSourceFactory implements DataSourceFactory {

    public SingleConnectionDataSourceFactory(){
        log.debug( "Initializing " + SingleConnectionDataSourceFactory.class );
    }

	@Override
	public DataSource newDataSource(String name, Config config) {
		try {
		    log.debug( "Creating new Datasource: " + name + ". " + config );
			final String className = config.getString("driver-class-name");
			final Class<?> aClass = Class.forName(className);
			final Driver driver = (Driver)aClass.newInstance();
			final String jdbcUrl = config.getString("jdbc-url");
			final Properties properties = new Properties();
			properties.putAll( config.toMap() );
			properties.put( "user", config.getString("username") );
			return new SimpleDataSource( driver, jdbcUrl, properties );
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}
}

@Slf4j
@RequiredArgsConstructor
class SimpleDataSource implements DataSource {

	final Driver driver;
	final String jdbcUrl;
	final Properties properties;

	@Override
	public Connection getConnection() throws SQLException {
	    log.debug( "SimpleDataSource: Creating new connection to " + jdbcUrl );
		return driver.connect( jdbcUrl, properties );
	}

	@Override
	public Connection getConnection(String s, String s1) throws SQLException {
        log.debug( "SimpleDataSource: Creating new connection to " + jdbcUrl + " with credentials" );
		throw new UnsupportedOperationException("Please, consider use SimpleDataSource.getConnection() method instead of this.");
	}

	@Override
	public <T> T unwrap(Class<T> aClass) throws SQLException {
        log.debug( "SimpleDataSource: Unwrapping " + aClass.getCanonicalName() );
		throw new UnsupportedOperationException("unwrap not implemented yet!");
	}

	@Override
	public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        log.debug( "SimpleDataSource: Will never be a wrapper " + aClass.getCanonicalName() );
		return false;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
        log.debug( "SimpleDataSource: Getting log writer" );
		throw new UnsupportedOperationException("getLogWriter not implemented yet!");
	}

	@Override
	public void setLogWriter(PrintWriter printWriter) throws SQLException {
        log.debug( "SimpleDataSource: Setting log writer" );
		throw new UnsupportedOperationException("setLogWriter not implemented yet!");
	}

	@Override
	public void setLoginTimeout(int i) throws SQLException {
        log.debug( "SimpleDataSource: setLoginTimeout" );
		throw new UnsupportedOperationException("setLoginTimeout not implemented yet!");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
        log.debug( "SimpleDataSource: getLoginTimeout" );
		throw new UnsupportedOperationException("getLoginTimeout not implemented yet!");
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        log.debug( "SimpleDataSource: getParentLogger" );
		throw new UnsupportedOperationException("getParentLogger not implemented yet!");
	}
}