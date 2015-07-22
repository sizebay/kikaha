package kikaha.core.security;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;

public interface IdentityManager {

	Account verify( final Credential credential );
}
