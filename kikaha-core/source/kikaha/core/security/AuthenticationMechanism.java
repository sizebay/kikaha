package kikaha.core.security;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Authentication mechanism interface based on {@code io.undertow.security.api.AuthenticationMechanism}
 * original implementation.
 *
 * @author miere.teixeira
 */
public interface AuthenticationMechanism {

    /**
     * Perform authentication of the request. Any potentially blocking work should be performed
     * in the handoff executor provided.
     *
     * @param exchange The exchange
     * @return
     */
	AuthenticationResponse authenticate(
    		final HttpServerExchange exchange, final Iterable<IdentityManager> identityManagers);

    @Getter
    @RequiredArgsConstructor
    public class AuthenticationResponse {
    	final AuthenticationOutcome outcome;
    	final Account account;
    }
}
