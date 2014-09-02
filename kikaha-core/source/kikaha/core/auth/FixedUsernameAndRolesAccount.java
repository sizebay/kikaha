package kikaha.core.auth;

import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class FixedUsernameAndRolesAccount implements Account {

	final Set<String> roles;
	final Principal principal;

	public FixedUsernameAndRolesAccount( final String username, final String role ) {
		this( createAdminRoles( role ), new FixedUsernamePrincipal( username ) );
	}

	static Set<String> createAdminRoles( final String role ) {
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