package kikaha.core.modules.security;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import kikaha.core.HttpServerExchangeStub;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FormAuthenticationMechanismTest {

	static final Credential CREDENTIAL = new UsernameAndPasswordCredential("username","password");
	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
	final FormData data = new FormData(2);

	@Mock IdentityManager identityManager;
	@Mock Session session;
	@Mock Account account;
	@Mock FormParserFactory formParserFactory;
	@Mock FormDataParser parser;

	@Before
	public void configureParser() throws IOException {
		doReturn( parser ).when( formParserFactory ).createParser( any() );
		doReturn( data ).when( parser ).parseBlocking();
		doReturn( account ).when( identityManager ).verify( eq(CREDENTIAL) );
	}

	@Test
	public void ensureThatIsAbleToSendCorrectCredentialsToIdentityManagerWhenFormFieldsArePresent(){
		data.add("j_username", "username");
		data.add("j_password", "password");
		final String originalUrl = "original-url.html";
		doReturn(originalUrl).when( session ).getAttribute(eq(FormAuthenticationMechanism.LOCATION_ATTRIBUTE));
		final AuthenticationMechanism mechanism = simulateLoginPost();
		final Account authenticated = mechanism.authenticate(exchange, singletonList(identityManager), session);
		assertNotNull(authenticated);
		assertHaveBeingRedirectedTo( originalUrl );
	}

	@Test
	public void ensureThatIsAbleRedirectBackToOriginalURLWhenFormFieldsArePresent(){
		data.add("j_username", "username");
		data.add("j_password", "password");
		final AuthenticationMechanism mechanism = simulateLoginPost();
		final Account authenticated = mechanism.authenticate(exchange, singletonList(identityManager), session);
		assertNotNull(authenticated);
	}

	@Test
	public void ensureThatNotSendCredentialsToIdentityManagerWhenNoFormFieldsArePresent(){
		final AuthenticationMechanism mechanism = simulateLoginPost();
		final Account authenticated = mechanism.authenticate(exchange, singletonList(identityManager), session);
		ensureNeverHaveTriedToAuthenticateThroughIdentityManager(authenticated);
	}

	@Test
	public void ensureThatSendRedirectionToLoginWhenNoFormFieldsArePresent(){
		final String postLocation = "/some/protected/place.html";
		final AuthenticationMechanism mechanism = simulateRequestTo(postLocation);
		assertTrue( mechanism.sendAuthenticationChallenge(exchange, session) );
		assertHaveBeingRedirectedTo("login.html");
	}

	private void assertHaveBeingRedirectedTo(String expectedLocation) {
		assertEquals(exchange.getStatusCode(), StatusCodes.FOUND);
		assertEquals(exchange.getResponseHeaders().get(Headers.LOCATION).getFirst(), expectedLocation);
	}

	@Test
	public void ensureThatCanMemorizedCurrentURLWhenNoFormFieldsArePresent(){
		final String postLocation = "/some/protected/place.html";
		final AuthenticationMechanism mechanism = simulateRequestTo(postLocation);
		assertNull( mechanism.authenticate(exchange, singletonList(identityManager), session) );
		verify( session ).setAttribute(eq(FormAuthenticationMechanism.LOCATION_ATTRIBUTE), eq(postLocation));
	}

	@Test
	public void ensureThatSendRedirectionToLoginErrorWhenNoCorrectCredentialsArePresentOnFormPost(){
		final AuthenticationMechanism mechanism = simulateLoginPost();
		assertTrue( mechanism.sendAuthenticationChallenge(exchange, session) );
		assertEquals(exchange.getStatusCode(), StatusCodes.FOUND);
		assertEquals(exchange.getResponseHeaders().get(Headers.LOCATION).getFirst(), "login-error.html");
	}

	void ensureNeverHaveTriedToAuthenticateThroughIdentityManager(final Account authenticated) {
		assertNull(authenticated);
		verify( identityManager, never() ).verify( any() );
	}

	AuthenticationMechanism simulateLoginPost(){
		final String defaultPostLocation = FormAuthenticationMechanism.DEFAULT_POST_LOCATION;
		return simulateRequestTo(defaultPostLocation);
	}

	AuthenticationMechanism simulateRequestTo(final String defaultPostLocation) {
		exchange.setRelativePath(defaultPostLocation);
		exchange.setRequestURI(defaultPostLocation);
		exchange.setRequestMethod(Methods.POST);
		return new FormAuthenticationMechanism( formParserFactory );
	}
}
