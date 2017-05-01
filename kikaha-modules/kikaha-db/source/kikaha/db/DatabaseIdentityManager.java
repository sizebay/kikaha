package kikaha.db;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import io.undertow.security.idm.Account;
import kikaha.config.Config;
import kikaha.core.modules.security.*;
import lombok.Getter;

/**
 *
 */
public class DatabaseIdentityManager extends AbstractPasswordBasedIdentityManager {

	@Inject SecurityConfiguration securityConfiguration;
	@Inject
	DataSourceProducer datasourceProducer;
	@Inject Config config;

	@Getter(lazy = true)
	private final String queryRetrieveUserPassword = config.getString( "server.auth.db-auth.select-user-password" );

	@Getter(lazy = true)
	private final String queryRetrieveUserRoles = config.getString( "server.auth.db-auth.select-user-roles" );

	@Getter(lazy = true)
	private final String dataSourceName = config.getString( "server.auth.db-auth.datasource" );

	@Getter(lazy = true)
	private final DataSource dataSource = datasourceProducer.produceViburDataSource( getDataSourceName() );

	@PostConstruct
	public void checkConfiguration(){
		final String retrieveUserPassword = getQueryRetrieveUserPassword();
		final String retrieveUserRoles = getQueryRetrieveUserRoles();
		if ( retrieveUserPassword == null || retrieveUserRoles == null )
			throw new UnsupportedOperationException( "No configuration defined for 'db-auth' IdentityManager" );
	}

	@Override
	public Account retrieveAccountFor( String id, String password ) {
		final PasswordEncoder encoder = securityConfiguration.getPasswordEncoder();
		final String storedPassword = retrieveUserPassword( id );

		Account account = null;
		if ( encoder.matches( password, storedPassword ) ) {
			final Set<String> roles = retrieveUserRoles( id );
			account = new FixedUsernameAndRolesAccount( id, roles );
		}

		return account;
	}

	private Set<String> retrieveUserRoles( String id ) {
		return retrieve( getQueryRetrieveUserRoles(), preparedStatement -> {
			preparedStatement.setString( 1, id );
			final ResultSet resultSet = preparedStatement.executeQuery();
			final Set<String> roles = new HashSet<>();
			while( resultSet.next() )
				roles.add( resultSet.getString( 1 ) );
			return roles;
		});
	}

	private String retrieveUserPassword( String id ) {
		return retrieve( getQueryRetrieveUserPassword(), preparedStatement -> {
			preparedStatement.setString( 1, id );
			final ResultSet resultSet = preparedStatement.executeQuery();
			if ( resultSet.next() )
				return resultSet.getString( 1 );
			return null;
		});
	}

	private <T> T retrieve( String sql, SQLFunction<PreparedStatement, T> function ) {
		try ( Connection connection = getDataSource().getConnection() ){
			final PreparedStatement preparedStatement = connection.prepareStatement( sql );
			return function.apply( preparedStatement );
		} catch ( SQLException e ) {
			throw new IllegalStateException( "Could not retrieve user roles", e );
		}
	}

	interface SQLFunction<T,R> {
		R apply( T t ) throws SQLException;
	}
}
