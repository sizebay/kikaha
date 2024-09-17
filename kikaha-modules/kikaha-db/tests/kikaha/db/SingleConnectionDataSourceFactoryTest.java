package kikaha.db;

import static org.junit.Assert.fail;
import java.sql.*;
import javax.inject.Inject;
import javax.sql.DataSource;
import kikaha.config.Config;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for {@link SingleConnectionDataSourceFactory}.
 */
@RunWith(KikahaRunner.class)
public class SingleConnectionDataSourceFactoryTest {

	@Inject SingleConnectionDataSourceFactory factory;
	@Inject Config config;

	@Test
	public void ensureCanConnectIntoDatabase() throws Exception {
		final Config config = this.config.getConfig("server.datasources.unit-test");
		final DataSource dataSource = factory.newDataSource("unit-test", config);
		try (final Connection connection = dataSource.getConnection() ) {
			final Statement statement = connection.createStatement();
			statement.execute( "SELECT password FROM users WHERE username = 'user'" );
		} catch ( Throwable cause ) {
			fail( "Could not connect using SingleConnectionDataSource: " + cause.getMessage() );
			cause.printStackTrace();
		}
	}
}