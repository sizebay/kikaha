package kikaha.db;

import static org.junit.Assert.*;
import javax.inject.*;
import javax.sql.DataSource;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( KikahaRunner.class )
public class DataSourceProducerTest {

	@Inject
	DataSource unnamedDatasource;

	@Inject
	@Named( "h2" )
	DataSource viburH2Datasource;

	@Inject
	@Named( "default" )
	DataSource defaultDatasource;

	@Test
	public void ensureThatDatasourceHaveReadAllConfigurationOptions() {
		assertNotNull(unnamedDatasource);
	}

	@Test
	public void ensureThatCanInjectNamedDatasources() {
		assertNotNull( viburH2Datasource );
	}

	@Test
	public void ensureThatCanInjectedNonNamedDatasources() {
		assertNotNull(unnamedDatasource);
	}

	@Test
	public void ensureThatInjectedNonNamedDatasourceIsTheDefaultDatasource() {
		assertSame( defaultDatasource, unnamedDatasource);
	}
}
