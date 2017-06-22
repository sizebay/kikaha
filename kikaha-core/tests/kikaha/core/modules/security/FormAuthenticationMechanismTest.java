package kikaha.core.modules.security;

import static java.util.Arrays.asList;
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
import kikaha.core.cdi.DefaultCDI;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FormAuthenticationMechanismTest {

	static final String CONTENT_TYPE_FORM = "multipart/form-data";
	static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	static final Credential CREDENTIAL = new UsernameAndPasswordCredential("username","password");
	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
	final DefaultAuthenticationConfiguration configuration = new DefaultCDI().load(DefaultAuthenticationConfiguration.class);
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
		final AuthenticationMechanism mechanism = simulateLoginPost(CONTENT_TYPE_FORM);
		final Account authenticated = mechanism.authenticate(exchange, singletonList(identityManager), session);
		assertNotNull(authenticated);
		assertHaveBeingRedirectedTo( "/" );
	}

	@Test
	public void ensureThatIsAbleToSendCorrectCredentialsToIdentityManagerWhenUsingFormUrlencodedContentType(){
		data.add("j_username", "username");
		data.add("j_password", "password");
		final AuthenticationMechanism mechanism = simulateLoginPost(CONTENT_TYPE_FORM_URLENCODED);
		final Account authenticated = mechanism.authenticate(exchange, singletonList(identityManager), session);
		assertNotNull(authenticated);
	}

	@Test
	public void ensureThatIsAbleRedirectBackToOriginalURLWhenFormFieldsArePresent(){
		data.add("j_username", "username");
		data.add("j_password", "password");
		final AuthenticationMechanism mechanism = simulateLoginPost(CONTENT_TYPE_FORM);
		final Account authenticated = mechanism.authenticate(exchange, singletonList(identityManager), session);
		assertNotNull(authenticated);
	}

	@Test
	public void ensureThatNotSendCredentialsToIdentityManagerWhenNoFormFieldsArePresent(){
		final AuthenticationMechanism mechanism = simulateLoginPost(CONTENT_TYPE_FORM);
		final Account authenticated = mechanism.authenticate(exchange, singletonList(identityManager), session);
		ensureNeverHaveTriedToAuthenticateThroughIdentityManager(authenticated);
	}

	@Test
	public void ensureThatNotSendCredentialsToIdentityManagerWhenNotFormContentTypeIsPresent(){
		data.add("j_username", "username");
		data.add("j_password", "password");
		final AuthenticationMechanism mechanism = simulateLoginPost( "application/json" );
		final Account authenticated = mechanism.authenticate(exchange, singletonList(identityManager), session);
		ensureNeverHaveTriedToAuthenticateThroughIdentityManager(authenticated);
	}

	@Test
	public void ensureThatSendRedirectionToLoginWhenNoFormFieldsArePresent(){
		final String postLocation = "/some/protected/place.html";
		final AuthenticationMechanism mechanism = simulateRequestTo(postLocation, CONTENT_TYPE_FORM);
		assertTrue( mechanism.sendAuthenticationChallenge(exchange, session) );
		assertHaveBeingRedirectedTo("/auth/");
	}

	private void assertHaveBeingRedirectedTo(String expectedLocation) {
		assertEquals(exchange.getStatusCode(),  StatusCodes.SEE_OTHER);
		assertEquals(exchange.getResponseHeaders().get(Headers.LOCATION).getFirst(), expectedLocation);
	}

	@Test
	public void ensureThatSendRedirectionToLoginErrorWhenNoCorrectCredentialsArePresentOnFormPost(){
		final AuthenticationMechanism mechanism = simulateLoginPost(CONTENT_TYPE_FORM);
		assertTrue( mechanism.sendAuthenticationChallenge(exchange, session) );
		assertEquals(exchange.getStatusCode(),  StatusCodes.SEE_OTHER);
		assertEquals(exchange.getResponseHeaders().get(Headers.LOCATION).getFirst(), "/auth/error/");
	}

	void ensureNeverHaveTriedToAuthenticateThroughIdentityManager(final Account authenticated) {
		assertNull(authenticated);
		verify( identityManager, never() ).verify( any() );
	}

	AuthenticationMechanism simulateLoginPost( String contentType ){
		return simulateRequestTo( "/auth/callback", contentType );
	}

	AuthenticationMechanism simulateRequestTo(String defaultPostLocation, String contentType) {
		exchange.setRelativePath(defaultPostLocation);
		exchange.setRequestURI(defaultPostLocation);
		exchange.setRequestMethod(Methods.POST);
		exchange.getRequestHeaders().add( Headers.CONTENT_TYPE, contentType );
		final FormAuthenticationMechanism mechanism = new FormAuthenticationMechanism(formParserFactory);
		mechanism.defaultAuthenticationConfiguration = configuration;
		mechanism.authenticate( exchange, asList(identityManager), session );
		return mechanism;
	}
}
