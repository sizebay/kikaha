package kikaha.core.auth;

import io.undertow.security.idm.Account;
import kikaha.core.api.conf.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import trip.spi.Provided;

@Accessors( fluent = true )
public class FixedUserAndPasswordIdentityManager
	extends AbstractPasswordBasedIdentityManager {

	@Provided
	Configuration configuration;

	@Getter( lazy = true )
	private final String username = configuredUsername();

	@Getter( lazy = true )
	private final String password = configuredPassword();

	@Getter( lazy = true )
	private final String role = configuredRole();

	@Override
	public Account retrieveAccountFor( String id, String password ) {
		if ( isValidIdAndPassword( id, password ) )
			return new FixedUsernameAndRolesAccount( username(), role() );
		return null;
	}

	boolean isValidIdAndPassword( String id, final String password ) {
		return username().equals( id )
			&& password().equals( password );
	}

	String configuredUsername() {
		return configuration.authentication().config().getString( "fixed-auth.username" );
	}

	String configuredPassword() {
		return configuration.authentication().config().getString( "fixed-auth.password" );
	}

	String configuredRole() {
		return configuration.authentication().config().getString( "fixed-auth.role" );
	}
}
