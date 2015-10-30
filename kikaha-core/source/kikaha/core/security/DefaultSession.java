package kikaha.core.security;

import io.undertow.security.idm.Account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefaultSession implements Session, Serializable {

	private static final long serialVersionUID = -8643108956697914235L;

	private final Map<String, Object> attributes = new HashMap<>();
	private Account authenticatedAccount;
	private boolean flushed = true;
	final String id;

	@Override
	public Object getAttribute( String name ) {
		return attributes.get( name );
	}

	@Override
	public Iterable<String> getAttributeNames() {
		return attributes.keySet();
	}

	@Override
	public void setAttribute( String name, Object value ) {
		flushed = true;
		attributes.put( name, value );
	}

	@Override
	public void setAuthenticatedAccount( Account account ) {
		flushed = true;
		authenticatedAccount = account;
	}

	@Override
	public Object removeAttribute( String name ) {
		return attributes.remove( name );
	}

	@Override
	public boolean hasChanged() {
		return flushed;
	}

	@Override
	public void flush() {
		flushed = false;
	}
}
