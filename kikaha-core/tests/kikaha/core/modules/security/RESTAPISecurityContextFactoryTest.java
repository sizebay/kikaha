package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotSame;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RESTAPISecurityContextFactoryTest {

	@Mock
	AuthenticationRule rule;

	HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Test
	public void ensureThatCreatesABrandNewEmptySessionStoreOnEveryRequest(){
		final RESTAPISecurityContextFactory factory = new RESTAPISecurityContextFactory();
		final DefaultSecurityContext firstSecurityContext = factory.createSecurityContextFor(exchange, rule);
		final DefaultSecurityContext secondSecurityContext = factory.createSecurityContextFor(exchange, rule);

		assertNotSame( firstSecurityContext, secondSecurityContext );
		assertNotSame( firstSecurityContext.getStore(), secondSecurityContext.getStore() );
	}
}
