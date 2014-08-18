package kikaha.hazelcast;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import kikaha.core.auth.AuthenticationRule;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;

public class AbstractSessionManagerBehaviorTest {

	final ServiceProvider provider = new ServiceProvider();
	final Cookie sessionIdCookie = new CookieImpl( SessionManager.SESSION_ID, SessionManager.generateSessionId() );
	final Session firefoxSession = new Session( "Firefox", null, null );
	final Session chromeSession = new Session( "Chrome", null, null );

	@Mock
	SecurityContext securityContext;

	@Provided
	SessionManager sessionManager;

	IMap<String, Session> sessionCache;

	protected void simulateThatReceivedCookieFromRequest() {
		doReturn( sessionIdCookie ).when( sessionManager ).getSessionCookie( any( HttpServerExchange.class ) );
		doNothing().when( sessionManager ).setSessionAsAttributeToExchange( any( HttpServerExchange.class ),
			eq( sessionIdCookie.getValue() ) );
	}

	protected void forceReturnMockedSecurityContext() {
		doReturn( securityContext ).when( sessionManager ).createSecurityContextWithDefaultFactory( any( HttpServerExchange.class ),
			any( AuthenticationRule.class ) );
	}

	protected void simulateThatHaveNotReceivedCookieFromRequest() {
		doReturn( null ).when( sessionManager ).getSessionCookie( any( HttpServerExchange.class ) );
	}

	protected void simulateAFirefoxUserAgentRequest() {
		doReturn( firefoxSession ).when( sessionManager )
			.createValidationSessionForExchange( any( HttpServerExchange.class ) );
	}

	protected void simulateAChromeUserAgentRequest() {
		doReturn( chromeSession ).when( sessionManager )
			.createValidationSessionForExchange( any( HttpServerExchange.class ) );
	}

	@Before
	public void setup() throws ServiceProviderException {
		provider.provideOn( this );
		MockitoAnnotations.initMocks( this );
		sessionCache = sessionManager.sessionCache
			= spy( sessionManager.sessionCache );
		sessionManager = spy( sessionManager );
	}

	protected void resetMocks() {
		try {
			provider.provideOn( this );
			MockitoAnnotations.initMocks( this );
			sessionManager = spy( sessionManager );
		} catch ( ServiceProviderException e ) {
			throw new RuntimeException( e );
		}
	}

	@After
	public void shutdownHazelcast() {
		Hazelcast.shutdownAll();
	}
}