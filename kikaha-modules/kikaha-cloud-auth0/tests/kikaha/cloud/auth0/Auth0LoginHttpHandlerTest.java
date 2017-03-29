package kikaha.cloud.auth0;

import static kikaha.cloud.auth0.Auth0Authentication.NONCE;
import static kikaha.cloud.auth0.Auth0LoginHttpHandler.CONTENT_TYPE_JSON;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import kikaha.core.SystemResource;
import kikaha.core.modules.security.*;
import kikaha.core.test.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 * Unit tests for Auth0LoginHttpHandler.
 */
@RunWith( KikahaRunner.class )
public class Auth0LoginHttpHandlerTest {

	static final String EXPECTED_PARSED_TEMPLATE = SystemResource.readFileAsString( "expected-parsed-template.html", "UTF-8" );

	@Inject Auth0LoginHttpHandler handler;

	@Mock Session currentSession;
	@Mock SecurityContext securityContext;
	HttpServerExchange httpExchange;

	@PostConstruct
	public void loadMocks(){
		MockitoAnnotations.initMocks( this );
		doReturn( currentSession ).when( securityContext ).getCurrentSession();
		httpExchange = HttpServerExchangeStub.createHttpExchange();
		httpExchange.setSecurityContext( securityContext );
	}

	@Test
	public void ensureCanPreComputeTheLoginHTMLTemplate(){
		final String parsedTemplate = handler.readAndParseTemplate();
		assertEquals( EXPECTED_PARSED_TEMPLATE, parsedTemplate );
	}

	@Test
	public void ensureCanSendLoginPageAsResponse() throws Exception {
		handler.handleRequest( httpExchange );

		verify( currentSession ).setAttribute( eq(NONCE), eq("") );
		verify( httpExchange.getResponseSender() ).send( eq( handler.html ) );
		assertEquals( 200, httpExchange.getStatusCode() );
		assertEquals( CONTENT_TYPE_JSON, httpExchange.getResponseHeaders().getFirst( Headers.CONTENT_TYPE ) );
	}
}