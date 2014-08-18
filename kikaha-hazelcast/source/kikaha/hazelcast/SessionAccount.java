package kikaha.hazelcast;

import io.undertow.security.idm.Account;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public class SessionAccount implements Account, Serializable {

	private static final long serialVersionUID = 2562185157305587686L;

	final SerializablePrincipal principal;
	final Set<String> roles;

	public static SessionAccount from( Account account ) {
		val principal = new SerializablePrincipal( account.getPrincipal().getName() );
		return new SessionAccount( principal, account.getRoles() );
	}

	public static SessionAccount empty() {
		return new SessionAccount( null, null );
	}
}

@Getter
@RequiredArgsConstructor
class SerializablePrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 2910910941792850418L;
	final String name;
	
}