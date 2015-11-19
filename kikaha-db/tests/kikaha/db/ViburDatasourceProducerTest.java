package kikaha.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import javax.sql.DataSource;

import kikaha.core.api.Source;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.vibur.dbcp.ViburDBCPDataSource;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;

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

	@Before
	public void startPool() {
		final DefaultServiceProvider provider = new DefaultServiceProvider();
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		provider.provideOn( this );
	}
}
