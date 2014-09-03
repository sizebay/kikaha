package kikaha.core.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;

import java.util.Set;

class EmptyUndertowSession implements Session {

	@Override
	public String getId() {
		return null;
	}

	@Override
	public void requestDone( HttpServerExchange serverExchange ) {
	}

	@Override
	public long getCreationTime() {
		return 0;
	}

	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	@Override
	public void setMaxInactiveInterval( int interval ) {
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public Object getAttribute( String name ) {
		return null;
	}

	@Override
	public Set<String> getAttributeNames() {
		return null;
	}

	@Override
	public Object setAttribute( String name, Object value ) {
		return null;
	}

	@Override
	public Object removeAttribute( String name ) {
		return null;
	}

	@Override
	public void invalidate( HttpServerExchange exchange ) {
	}

	@Override
	public SessionManager getSessionManager() {
		return null;
	}

	@Override
	public String changeSessionId( HttpServerExchange exchange, SessionConfig config ) {
		return null;
	}
	
}