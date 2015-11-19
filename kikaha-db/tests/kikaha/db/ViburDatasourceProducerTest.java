package kikaha.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import javax.sql.DataSource;

import kikaha.core.api.Source;
import kikaha.core.test.KikahaRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.vibur.dbcp.ViburDBCPDataSource;

import trip.spi.Provided;

@RunWith( KikahaRunner.class )
public class ViburDatasourceProducerTest {

	@Provided
	ViburDBCPDataSource viburDatasource;

	@Provided
	@Source( "h2" )
	ViburDBCPDataSource viburH2Datasource;

	@Provided
	@Source( "default" )
	ViburDBCPDataSource defaultViburDatasource;

	@Provided
	@Source( "default" )
	DataSource defaultDatasource;

	@Test
	public void ensureThatDatasourceHaveReadAllConfigurationOptions() {
		assertNotNull( viburDatasource );
	}

	@Test
	public void ensureThatCanInjectNamedDatasources() {
		assertNotNull( viburH2Datasource );
	}

	@Test
	public void ensureThatCanInjectedNonNamedDatasources() {
		assertNotNull( viburDatasource );
	}

	@Test
	public void ensureThatInjectedNonNamedDatasourceIsTheDefaultDatasource() {
		assertSame( defaultViburDatasource, viburDatasource );
	}

	@Test
	public void ensureThatInjectedDataSourceIsTheSameInjectedViburDataSource() {
		assertSame( defaultViburDatasource, defaultDatasource );
	}
}
