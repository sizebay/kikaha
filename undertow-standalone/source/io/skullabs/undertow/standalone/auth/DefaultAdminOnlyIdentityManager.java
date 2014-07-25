package io.skullabs.undertow.standalone.auth;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;

public class DefaultAdminOnlyIdentityManager implements IdentityManager {

	@Override
	public Account verify( Account account ) {
		return null;
	}

	@Override
	public Account verify( String id, Credential credential ) {
		return null;
	}

	@Override
	public Account verify( Credential credential ) {
		return null;
	}
}
