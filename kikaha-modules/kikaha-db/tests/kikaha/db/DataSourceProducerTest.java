package kikaha.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import kikaha.core.test.KikahaRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.vibur.dbcp.ViburDBCPDataSource;

@RunWith( KikahaRunner.class )
public class DataSourceProducerTest {

	@Inject
	ViburDBCPDataSource viburDatasource;

	@Inject
	@Named( "h2" )
	ViburDBCPDataSource viburH2Datasource;

	@Inject
	@Named( "default" )
	ViburDBCPDataSource defaultViburDatasource;

	@Inject
	@Named( "default" )
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
