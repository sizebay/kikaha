package kikaha.db;

import javax.inject.Inject;
import java.util.*;
import io.undertow.security.idm.Account;
import kikaha.core.modules.security.*;

/**
 *
 */
public class DatabaseIdentityManager extends AbstractPasswordBasedIdentityManager {

	@Inject SecurityConfiguration securityConfiguration;

	@Override
	public Account retrieveAccountFor( String id, String password ) {
		final PasswordEncoder encoder = securityConfiguration.getPasswordEncoder();
		final String storedPassword = retrieveUserPassword( id );

		Account account = null;
		if ( encoder.matches( password, storedPassword ) ) {
			final Set<String> roles = retrieveUserRoles( id );
			account = new FixedUsernameAndRolesAccount( id, roles );
		}

		return ;
	}

	private Set<String> retrieveUserRoles( String id ) {
		return null;
	}

	private String retrieveUserPassword( String id ) {
		return null;
	}
}
