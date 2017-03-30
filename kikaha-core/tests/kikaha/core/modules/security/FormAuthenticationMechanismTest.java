package kikaha.core.modules.security;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.io.IOException;
import io.undertow.security.idm.*;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.*;
import io.undertow.util.*;
import kikaha.core.cdi.DefaultServiceProvider;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FormAuthenticationMechanismTest {

	static final Credential CREDENTIAL = new UsernameAndPasswordCredential("username","password");
	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
	final FormAuthenticationConfiguration configuration = new DefaultServiceProvider().load(FormAuthenticationConfiguration.class);
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
		final FormAuthenticationMechanism mechanism = new FormAuthenticationMechanism(formParserFactory);
		mechanism.formAuthenticationConfiguration = configuration;
		return mechanism;
	}
}
