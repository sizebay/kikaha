package kikaha.core.modules.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import java.util.Date;
import javax.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import kikaha.core.test.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class DefaultSecurityContextTest {

	@Inject DefaultSecurityContextFactory factory;
	@Inject SecurityConfiguration configuration;

	@Test
	public void ensureLogoutWillSendExpirationCookie() throws Exception {
		final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		final AuthenticationRule authenticationRule = mock(AuthenticationRule.class);
		final DefaultSecurityContext context = factory.createSecurityContextFor(exchange, authenticationRule, configuration);
		context.logout();

		final Cookie jsessionid = exchange.getResponseCookies().get("JSESSIONID");
		assertEquals( -1,  jsessionid.getExpires().compareTo( new Date() ) );
	}
}