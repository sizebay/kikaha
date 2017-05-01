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
public class SingleConnectionDataSourceFactory implements DataSourceFactory {

	@Override
	public DataSource newDataSource(String name, Config config) {
		try {
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
		return driver.connect( jdbcUrl, properties );
	}

	@Override
	public Connection getConnection(String s, String s1) throws SQLException {
		throw new UnsupportedOperationException("Please, consider use SimpleDataSource.getConnection() method instead of this.");
	}

	@Override
	public <T> T unwrap(Class<T> aClass) throws SQLException {
		throw new UnsupportedOperationException("unwrap not implemented yet!");
	}

	@Override
	public boolean isWrapperFor(Class<?> aClass) throws SQLException {
		return false;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException("getLogWriter not implemented yet!");
	}

	@Override
	public void setLogWriter(PrintWriter printWriter) throws SQLException {
		throw new UnsupportedOperationException("setLogWriter not implemented yet!");
	}

	@Override
	public void setLoginTimeout(int i) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout not implemented yet!");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException("getLoginTimeout not implemented yet!");
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException("getParentLogger not implemented yet!");
	}
}