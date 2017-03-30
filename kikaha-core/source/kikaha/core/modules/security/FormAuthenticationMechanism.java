package kikaha.core.modules.security;

import java.io.IOException;
import javax.inject.*;
import io.undertow.security.idm.*;
import io.undertow.server.*;
import io.undertow.server.handlers.form.*;
import io.undertow.util.*;
import kikaha.config.Config;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Singleton
@RequiredArgsConstructor
public class FormAuthenticationMechanism implements AuthenticationMechanism {

	public static final String LOCATION_ATTRIBUTE = FormAuthenticationMechanism.class.getName() + ".LOCATION";
	public static final String DEFAULT_POST_LOCATION = "j_security_check";
	private final FormParserFactory formParserFactory;

	@Inject FormAuthenticationConfiguration formAuthenticationConfiguration;
	@Inject Config config;

	public FormAuthenticationMechanism(){
		formParserFactory = FormParserFactory.builder().build();
	}

	@Override
	public Account authenticate(HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session) {
		Account account = null;
		try {
			if ( isCurrentRequestTryingToAuthenticate(exchange) )
				account = doAuthentication(exchange, identityManagers, session);
		} catch (final IOException e) {
			log.error("Failed to authenticate. Skipping form authentication...", e);
		}
		return account;
	}

	private Account doAuthentication( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session) throws IOException {
		final Credential credential = readCredentialFromRequest(exchange);
		Account account = null;
		if ( credential != null )
			account = verify( identityManagers, credential );
		if ( account != null )
			sendRedirectBack( exchange );
		return account;
	}

	private void sendRedirectBack(HttpServerExchange exchange) {
		sendRedirect(exchange, formAuthenticationConfiguration.getSuccessPage() );
	}

	private Credential readCredentialFromRequest(HttpServerExchange exchange) throws IOException {
		Credential credential = null;
		final FormDataParser parser = formParserFactory.createParser(exchange);
		final FormData data = parser.parseBlocking();
        final FormData.FormValue jUsername = data.getFirst("j_username");
        final FormData.FormValue jPassword = data.getFirst("j_password");
        if (jUsername != null && jPassword != null) {
        	final String userName = jUsername.getValue();
        	final String password = jPassword.getValue();
            credential = new UsernameAndPasswordCredential(userName, password);
        }
		return credential;
	}

	@Override
	public boolean sendAuthenticationChallenge(HttpServerExchange exchange, Session session) {
		final String newLocation = isCurrentRequestTryingToAuthenticate(exchange)
				? formAuthenticationConfiguration.getErrorPage()
				: formAuthenticationConfiguration.getLoginPage();
		sendRedirect(exchange, newLocation);
		return true;
	}

	private boolean isCurrentRequestTryingToAuthenticate(HttpServerExchange exchange){
		return isPostLocation(exchange) && exchange.getRequestMethod().equals(Methods.POST);
	}

	private boolean isPostLocation(HttpServerExchange exchange) {
		return exchange.getRelativePath().equals( formAuthenticationConfiguration.getCallbackUrl() );
	}

	private static void sendRedirect(HttpServerExchange exchange, final String location) {
		if ( !exchange.isResponseStarted() ) {
			exchange.setStatusCode(StatusCodes.FOUND);
			exchange.getResponseHeaders().put(Headers.LOCATION, location);
			exchange.endExchange();
		} else {
			log.error("Could not redirect. Response already started.");
		}
	}
}