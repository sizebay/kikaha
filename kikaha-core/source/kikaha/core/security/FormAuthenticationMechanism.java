package kikaha.core.security;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;

import java.io.IOException;

import javax.annotation.PostConstruct;

import kikaha.core.api.conf.Configuration;
import kikaha.core.api.conf.FormAuthConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trip.spi.Provided;
import trip.spi.Singleton;

@Getter
@Slf4j
@Singleton
@RequiredArgsConstructor
public class FormAuthenticationMechanism implements AuthenticationMechanism {

	public static final String LOCATION_ATTRIBUTE = FormAuthenticationMechanism.class.getName() + ".LOCATION";
	public static final String DEFAULT_POST_LOCATION = "/j_security_check";

	@NonNull private String loginPage = "login.html";
	@NonNull private String errorPage = "login-error.html";
	@NonNull private String postLocation = DEFAULT_POST_LOCATION;

	private final FormParserFactory formParserFactory;

	@Provided
	Configuration kikahaConf;

	public FormAuthenticationMechanism() {
		this.formParserFactory = FormParserFactory.builder().build();
	}

	@PostConstruct
	public void readConfiguration() {
		final FormAuthConfiguration formAuth = kikahaConf.authentication().formAuth();
		this.loginPage = formAuth.loginPage();
		this.errorPage = formAuth.errorPage();
		this.postLocation = formAuth.postLocation();
	}

	@Override
	public Account authenticate(HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session) {
		Account account = null;
		try {
			if ( isCurrentRequestTryingToAuthenticate(exchange) )
				account = doAuthentication(exchange, identityManagers, session);
			else if ( !isPostLocation(exchange) )
				memorizeCurrentPage( exchange, session );
		} catch (final IOException e) {
			log.error("Failed to authenticate. Skipping form authentication...", e); }
		return account;
	}

	private Account doAuthentication( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session) throws IOException {
		final Credential credential = readCredentialFromRequest(exchange);
		Account account = null;
		if ( credential != null )
			account = verify( identityManagers, credential );
		if ( account != null )
			sendRedirectBack( exchange, session );
		return account;
	}

	private static void sendRedirectBack(HttpServerExchange exchange, Session session) {
		final String location = (String)session.getAttribute(LOCATION_ATTRIBUTE);
		sendRedirect(exchange, location);
	}

	private static void memorizeCurrentPage( HttpServerExchange exchange, Session session ){
		final String location = exchange.getRequestURI();
		session.setAttribute(LOCATION_ATTRIBUTE, location);
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
				? errorPage : loginPage;
		sendRedirect(exchange, newLocation);
		return true;
	}

	private boolean isCurrentRequestTryingToAuthenticate(HttpServerExchange exchange){
		return isPostLocation(exchange) && exchange.getRequestMethod().equals(Methods.POST);
	}

	private boolean isPostLocation(HttpServerExchange exchange) {
		return exchange.getRelativePath().endsWith(postLocation);
	}

	private static void sendRedirect(HttpServerExchange exchange, final String location) {
		exchange.addDefaultResponseListener( RedirectBack.to(location) );
		exchange.endExchange();
	}
}

@RequiredArgsConstructor(staticName="to")
class RedirectBack implements DefaultResponseListener {

	final String location;

	@Override
	public boolean handleDefaultResponse(HttpServerExchange exchange) {
		exchange.getResponseHeaders().put(Headers.LOCATION, location);
		exchange.setResponseCode(StatusCodes.FOUND);
        exchange.endExchange();
        return true;
	}
}