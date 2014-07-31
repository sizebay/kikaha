package io.skullabs.undertow.standalone.auth;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;

public class DefaultAdminOnlyIdentityManager implements IdentityManager {

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
				return new AdminAccount();
		}
		return null;
	}

	private boolean isValidIdAndPassword( String id, final java.lang.String password ) {
		return AdminPrincipal.ADMIN.equals( id )
				&& AdminPrincipal.ADMIN.equals( password );
	}

	@Override
	public Account verify( Credential credential ) {
		return null;
	}
}

@Getter
@EqualsAndHashCode
class AdminAccount implements Account {

	final Set<String> roles = createAdminRoles();
	final Principal principal = new AdminPrincipal();

	private Set<String> createAdminRoles() {
		val roles = new HashSet<String>();
		roles.add( AdminPrincipal.ADMIN );
		roles.add( "minimum-access-role" );
		return roles;
	}
}

@Getter
@EqualsAndHashCode
class AdminPrincipal implements Principal {

	static final String ADMIN = "admin";

	final String name = ADMIN;
}