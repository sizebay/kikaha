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
	final Cookie sessionIdCookie = new CookieImpl( SessionCacheManager.SESSION_ID, SessionCacheManager.generateSessionId() );
	final AuthenticatedSession firefoxSession = new AuthenticatedSession( null, "Firefox", null, null );
	final AuthenticatedSession chromeSession = new AuthenticatedSession( null, "Chrome", null, null );

	@Mock
	SecurityContext securityContext;

	@Mock
	IMap<String, AuthenticatedSession> cache;

	@Provided
	SessionCacheManager sessionCache;

	@Provided
	HazelcastSecurityContextFactory factory;

	protected void simulateThatReceivedCookieFromRequest() {
		doReturn( sessionIdCookie ).when( sessionCache ).getSessionCookie( any( HttpServerExchange.class ) );
		doNothing().when( sessionCache ).setSessionAsAttributeToExchange( any( HttpServerExchange.class ),
			eq( sessionIdCookie.getValue() ) );
		doReturn( true ).when( sessionCache ).isValidSessionForExchange(
			any( AuthenticatedSession.class ),
			any( HttpServerExchange.class ) );
	}

	protected void forceReturnMockedSecurityContext() {
		doReturn( securityContext ).when( factory ).createSecurityContextWithDefaultFactory(
			any( HttpServerExchange.class ), any( AuthenticationRule.class ) );
	}

	protected void simulateThatHaveNotReceivedCookieFromRequest() {
		doReturn( null ).when( sessionCache ).getSessionCookie( any( HttpServerExchange.class ) );
	}

	protected void simulateAFirefoxUserAgentRequest() {
		doReturn( firefoxSession ).when( sessionCache )
			.createValidationSessionForExchange( any( HttpServerExchange.class ) );
	}

	protected void simulateAChromeUserAgentRequest() {
		doReturn( chromeSession ).when( sessionCache )
			.createValidationSessionForExchange( any( HttpServerExchange.class ) );
	}

	@Before
	public void setup() throws ServiceProviderException {
		provider.provideOn( this );
		MockitoAnnotations.initMocks( this );
		sessionCache = spy( sessionCache );
		factory = spy( factory );
		factory.sessionCache = sessionCache;
		doReturn( cache ).when( sessionCache ).produceSessionCache();
		// doReturn( "HZ29BCB1391F4F4F468A2E339F32CEFC5F" ).when( sessionCache
		// ).generateANewId();
	}

	protected void resetMocks() {
		try {
			provider.provideOn( this );
			MockitoAnnotations.initMocks( this );
			sessionCache = spy( sessionCache );
			doReturn( cache ).when( sessionCache ).produceSessionCache();
			factory = spy( factory );
			factory.sessionCache = sessionCache;
		} catch ( ServiceProviderException e ) {
			throw new RuntimeException( e );
		}
	}

	@After
	public void shutdownHazelcast() {
		Hazelcast.shutdownAll();
	}
}