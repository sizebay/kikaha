package kikaha.db;

import javax.sql.DataSource;
import kikaha.config.Config;

/**
 *
 */
public interface DataSourceFactory {

	DataSource newDataSource( String name, Config config );
}
