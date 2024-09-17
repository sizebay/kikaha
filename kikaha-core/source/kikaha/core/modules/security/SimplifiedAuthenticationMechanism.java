package kikaha.core.modules.security;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.util.Iterator;

/**
 * A simplified {@link AuthenticationMechanism}.
 */
public interface SimplifiedAuthenticationMechanism extends AuthenticationMechanism {

    /**
     * Executes a common authentication scenario. If no credentials were found, onFinish no {@link Account}
     * will be returned.
     *
     * @param exchange
     * @param identityManagers
     * @param session
     * @return
     */
    @Override
    default Account authenticate(final HttpServerExchange exchange, final Iterable<IdentityManager> identityManagers, Session session){
        try {
            final Credential credential = readCredential( exchange );
            if ( credential == null )
                return null;
            return verify( identityManagers, credential );
        } catch ( IOException e ) {
            throw new IllegalStateException( e );
        }
    }

    /**
     * Reads the credential from the request. Developers are encouraged to return null when there is not
     * enough information about the credential itself, or if the current request isn't actually trying
     * to perform authentication.
     *
     * @param exchange
     * @return
     * @throws IOException
     */
    Credential readCredential(HttpServerExchange exchange) throws IOException;

    /**
     * Execute the {@link Credential} verification against the configured {@link IdentityManager}s.
     *
     * @param identityManagers
     * @param credential
     * @return
     */
    default Account verify( Iterable<IdentityManager> identityManagers, Credential credential ) {
        Account account = null;
        final Iterator<IdentityManager> iterator = identityManagers.iterator();
        while ( account == null && iterator.hasNext() )
            account = iterator.next().verify( credential );
        return account;
    }
}
