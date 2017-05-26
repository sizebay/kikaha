package kikaha.core.modules.security;

import static java.util.Arrays.asList;
import static kikaha.core.modules.security.SecurityEventListener.SecurityEventType.LOGIN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultSecurityContextFactoryTest {

	final DefaultSecurityContextFactory factory = new DefaultSecurityContextFactory();
	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Mock SessionIdManager manager;
	@Mock SessionStore store;
	@Mock Session session;
	@Mock AuthenticationRule rule;
	@Mock SecurityEventListener eventListener;

	@Spy @InjectMocks
	SecurityConfiguration securityConfiguration;

	@Before
	public void configureMocks(){
		doReturn( session ).when( store ).createOrRetrieveSession( eq(exchange), any() );
	}

	@Test
	public void ensureAuthenticationIsRequiredWhenAuthenticationRuleIsNotEmpty(){
		final DefaultSecurityContext securityContext = factory.createSecurityContextFor(exchange, rule, securityConfiguration);
		assertTrue( securityContext.isAuthenticationRequired() );
	}

	@Test
	public void ensureAuthenticationIsNotRequiredWhenAuthenticationRuleIsEmpty(){
		final DefaultSecurityContext securityContext = factory.createSecurityContextFor(exchange, AuthenticationRule.EMPTY, securityConfiguration);
		assertFalse( securityContext.isAuthenticationRequired() );
	}

	@Test
	public void ensureIsAbleToNotifyEventListeners(){
		doReturn( asList( eventListener ) ).when( securityConfiguration ).getEventListeners();

		final DefaultSecurityContext securityContext = factory.createSecurityContextFor(exchange, rule, securityConfiguration);
		securityContext.notifySecurityEvent( LOGIN );

		verify( eventListener, times(1) ).onEvent( eq( LOGIN ), eq( exchange ), eq(session) );
	}
}