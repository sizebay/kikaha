package kikaha.core.modules.security;

import static org.junit.Assert.assertNotSame;
import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RESTAPISecurityContextFactoryTest {

	@Mock AuthenticationRule rule;
	@Mock SessionStore store;
	@Mock SessionIdManager manager;

	HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Test
	public void ensureThatCreatesABrandNewEmptySessionStoreOnEveryRequest(){
		final RESTAPISecurityContextFactory factory = new RESTAPISecurityContextFactory();
		final DefaultSecurityContext firstSecurityContext = factory.createSecurityContextFor(exchange, rule, store, manager);
		final DefaultSecurityContext secondSecurityContext = factory.createSecurityContextFor(exchange, rule, store, manager);

		assertNotSame( firstSecurityContext, secondSecurityContext );
		assertNotSame( firstSecurityContext.getStore(), secondSecurityContext.getStore() );
	}
}
