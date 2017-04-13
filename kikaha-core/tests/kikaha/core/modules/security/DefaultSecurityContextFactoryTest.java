package kikaha.core.modules.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
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
	@Mock AuthenticationRule rule;

	@Spy @InjectMocks
	SecurityConfiguration securityConfiguration;

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
}