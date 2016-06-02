package kikaha.core.modules.security;

import java.io.Serializable;
import java.util.*;
import io.undertow.security.idm.Account;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class DefaultSession implements Session, Serializable {

	private static final long serialVersionUID = -8643108956697914235L;

	private final Map<String, Object> attributes = new HashMap<>();
	private Account authenticatedAccount;
	private boolean changed = true;
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
		changed = true;
		attributes.put( name, value );
	}

	@Override
	public void setAuthenticatedAccount( Account account ) {
		changed = true;
		authenticatedAccount = account;
	}

	@Override
	public Object removeAttribute( String name ) {
		changed = true;
		return attributes.remove( name );
	}

	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public void flush() {
		changed = false;
	}
}
