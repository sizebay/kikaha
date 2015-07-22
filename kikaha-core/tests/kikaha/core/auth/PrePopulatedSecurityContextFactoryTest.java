package kikaha.core.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import kikaha.core.HttpServerExchangeStub;
import kikaha.core.impl.conf.DefaultAuthenticationConfiguration;
import kikaha.core.impl.conf.DefaultConfiguration;
import kikaha.core.security.SecurityContextFactory;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import trip.spi.DefaultServiceProvider;

public class PrePopulatedSecurityContextFactoryTest {

	@Mock
	SecurityContext securityContext;

	@Mock
	SecurityContextFactory wrappedFactory;
	PrePopulatedSecurityContextFactory finalFactory;

	HttpServerExchange exchange;
	AuthenticationRule matchedRule;

	@Test
	public void ensureThatAreAbleToRegisterAllAuthenticationMechanismsToSecurityContext() throws Exception {
		when( securityContext.authenticate() ).thenReturn( true );
		finalFactory.createSecurityContextFor( exchange, matchedRule );
		verify( securityContext ).addAuthenticationMechanism( matchedRule.mechanisms().get( 0 ) );
		verify( finalFactory ).setEmptyUndertowSessionManagerOnExchange( any( HttpServerExchange.class ) );
	}

	@Test
	public void ensureThatIsListenForAuthenticationEvents() throws Exception {
		val notificationReceiver = mock( NotificationReceiver.class );
		doReturn( notificationReceiver ).when( matchedRule ).notificationReceiver();
		doReturn( true ).when( matchedRule ).isThereSomeoneListeningForAuthenticationEvents();
		finalFactory.createSecurityContextFor( exchange, matchedRule );
		verify( securityContext ).registerNotificationReceiver( matchedRule.notificationReceiver() );
	}

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		initializeExchange();
		initializeFactories();
	}

	void initializeExchange() {
		this.exchange = HttpServerExchangeStub.createHttpExchange();
	}

	void initializeFactories() {
		val matcher = mockAuthRuleMatcher();
		this.matchedRule = spy( matcher.retrieveAuthenticationRuleForUrl( "/sample-route" ) );
		doReturn( securityContext ).when( wrappedFactory ).createSecurityContextFor( exchange, matchedRule );
		finalFactory = spy( PrePopulatedSecurityContextFactory.wrap( wrappedFactory ) );
	}

	AuthenticationRuleMatcher mockAuthRuleMatcher() {
		val defaultConfig = DefaultConfiguration.loadDefaultConfig().getConfig( "server.auth" );
		val authConfig = new DefaultAuthenticationConfiguration( defaultConfig );
		val provider = new DefaultServiceProvider();
		val authRuleMatcher = spy( new AuthenticationRuleMatcher( provider, authConfig ) );
		return authRuleMatcher;
	}
}
