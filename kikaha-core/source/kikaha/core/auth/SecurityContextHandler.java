package kikaha.core.auth;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;

import java.security.AccessController;
import java.security.PrivilegedAction;

public interface SecurityContextHandler {

	public static SecurityContextHandler DEFAULT = new DefaultSecurityContextHandler();
	public static SecurityContextHandler PRIVILEGED = new PrivilegedSecurityContextHandler();

	void setSecurityContext( final HttpServerExchange exchange, final SecurityContext securityContext );

}

class DefaultSecurityContextHandler implements SecurityContextHandler {

	@Override
	public void setSecurityContext( HttpServerExchange exchange, SecurityContext securityContext ) {
		exchange.setSecurityContext( securityContext );
	}
}

class PrivilegedSecurityContextHandler implements SecurityContextHandler {

	@Override
	public void setSecurityContext( final HttpServerExchange exchange, final SecurityContext securityContext ) {
		AccessController.doPrivileged( new PrivilegedAction<Object>() {
			@Override
			public Object run() {
				exchange.setSecurityContext( securityContext );
				return null;
			}
		} );
	}
}