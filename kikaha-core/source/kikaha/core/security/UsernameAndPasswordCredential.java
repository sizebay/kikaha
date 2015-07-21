package kikaha.core.security;

import io.undertow.security.idm.Credential;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UsernameAndPasswordCredential implements Credential {

	final String username;
	final String password;
}
