package kikaha.core.auth;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import kikaha.core.api.Configuration;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;
import trip.spi.Provided;

@Accessors( fluent = true )
public class FixedUserAndPasswordIdentityManager implements IdentityManager {

	@Provided
	Configuration configuration;

	@Getter( lazy = true )
	private final String username = configuredUsername();

	@Getter( lazy = true )
	private final String password = configuredPassword();

	@Getter( lazy = true )
	private final String role = configuredRole();

	@Override
	public Account verify( Account account ) {
		return account;
	}

	@Override
	public Account verify( String id, Credential credential ) {
		if ( credential instanceof PasswordCredential ) {
			val passwordCredential = (PasswordCredential)credential;
			val password = new String( passwordCredential.getPassword() );
			if ( isValidIdAndPassword( id, password ) )
				return new FixedUsernameAndRolesAccount( username(), role() );
		}
		return null;
	}

	private boolean isValidIdAndPassword( String id, final String password ) {
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

	@Override
	public Account verify( Credential credential ) {
		return null;
	}
}

@Getter
@EqualsAndHashCode
class FixedUsernameAndRolesAccount implements Account {

	final Set<String> roles;
	final Principal principal;

	public FixedUsernameAndRolesAccount( final String username, final String role ) {
		this.roles = createAdminRoles( role );
		this.principal = new FixedUsernamePrincipal( username );
	}

	private Set<String> createAdminRoles( final String role ) {
		val roles = new HashSet<String>();
		roles.add( role );
		roles.add( "minimum-access-role" );
		return roles;
	}
}

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
class FixedUsernamePrincipal implements Principal {

	final String name;
}