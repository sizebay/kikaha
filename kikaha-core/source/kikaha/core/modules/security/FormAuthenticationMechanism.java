package kikaha.core.modules.security;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import kikaha.core.modules.undertow.Redirect;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Getter
@Slf4j
@Singleton
@RequiredArgsConstructor
public class FormAuthenticationMechanism implements SimplifiedAuthenticationMechanism {

	public static final String LOCATION_ATTRIBUTE = FormAuthenticationMechanism.class.getName() + ".LOCATION";
	private final FormParserFactory formParserFactory;

	@Inject
	AuthenticationEndpoints authenticationEndpoints;
	@Inject FormAuthenticationRequestMatcher matcher;

	public FormAuthenticationMechanism(){
		formParserFactory = FormParserFactory.builder().build();
	}

	@Override
	public Account authenticate(HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session) {
		Account account = null;
		try {
			if ( isCurrentRequestTryingToAuthenticate(exchange) )
				account = doAuthentication(exchange, identityManagers);
		} catch (final IOException e) {
			log.error("Failed to authenticate. Skipping form authentication...", e);
		}
		return account;
	}

	private Account doAuthentication(HttpServerExchange exchange, Iterable<IdentityManager> identityManagers) throws IOException {
		final Credential credential = readCredential(exchange);
		Account account = null;
		if ( credential != null )
			account = verify( identityManagers, credential );
		if ( account != null )
			sendRedirectBack( exchange );
		return account;
	}

	private void sendRedirectBack(HttpServerExchange exchange) {
		Redirect.to(exchange, authenticationEndpoints.getSuccessPage() );
	}

	@Override
	public Credential readCredential(HttpServerExchange exchange) throws IOException {
		if ( !exchange.isBlocking() )
			exchange.startBlocking();

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
				? authenticationEndpoints.getErrorPage()
				: authenticationEndpoints.getLoginPage();
		Redirect.to(exchange, newLocation);
		return true;
	}

	private boolean isCurrentRequestTryingToAuthenticate(HttpServerExchange exchange){
		return isPostLocation(exchange) && exchange.getRequestMethod().equals(Methods.POST) && isContentTypeForm(exchange);
	}

	private boolean isContentTypeForm(HttpServerExchange exchange) {
		final String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
		return "multipart/form-data".equals( contentType ) || "application/x-www-form-urlencoded".equals( contentType );
	}

	private boolean isPostLocation(HttpServerExchange exchange) {
		return exchange.getRelativePath().equals( authenticationEndpoints.getCallbackUrl() );
	}

	@Override
	public void configure(SecurityConfiguration securityConfiguration, AuthenticationEndpoints authenticationConfiguration) {
		securityConfiguration.setRequestMatcherIfAbsent( matcher );
	}
}