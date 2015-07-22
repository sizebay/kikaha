package kikaha.core.security;

import io.undertow.security.idm.Credential;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UsernameAndPasswordCredential implements Credential {

	final String username;
	final String password;

	@Override
	public boolean equals(final Object obj) {
		if ( obj instanceof UsernameAndPasswordCredential ) {
			final UsernameAndPasswordCredential other= (UsernameAndPasswordCredential)obj;
			return username.equals(other.username)
				&& password.equals(other.password);
		}
		return super.equals(obj);
	}
}
