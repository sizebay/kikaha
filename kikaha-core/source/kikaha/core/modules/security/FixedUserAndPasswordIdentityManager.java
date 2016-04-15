package kikaha.core.modules.security;

import io.undertow.security.idm.Account;
import kikaha.config.Config;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@Accessors( fluent = true )
public class FixedUserAndPasswordIdentityManager
	extends AbstractPasswordBasedIdentityManager {

	@Inject
	Config config;

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
		return config.getString( "server.auth.fixed-auth.username" );
	}

	String configuredPassword() {
		return config.getString( "server.auth.fixed-auth.password" );
	}

	String configuredRole() {
		return config.getString( "server.auth.fixed-auth.role" );
	}
}
