package kikaha.core.modules.security;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import javax.inject.Inject;
import io.undertow.server.*;
import kikaha.config.Config;
import kikaha.core.cdi.ServiceProvider;
import kikaha.core.test.*;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

@RunWith( KikahaRunner.class )
public class AuthenticationHttpHandlerTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Mock SecurityContext securityContext;
	@Mock HttpHandler rootHandler;
	@Mock SecurityContextFactory factory;

	@Inject AuthenticationModule deployment;
	@Inject ServiceProvider provider;
	@Inject Config config;

	AuthenticationHttpHandler authenticationHook;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		AuthenticationRuleMatcher authenticationRuleMatcher = new AuthenticationRuleMatcher( provider, config.getConfig("server.auth") );
		authenticationHook = spy( new AuthenticationHttpHandler( authenticationRuleMatcher, "",
				rootHandler, factory, deployment.sessionStore, deployment.sessionIdManager ) );
	}

	@Test
	@SneakyThrows
	public void ensureThatCallTheHookInIOThreadWhenHasRuleThatMatchesTheRelativePath() {
		doNothing().when(authenticationHook).runAuthenticationInIOThread( any(), any(), any());
		exchange.setRelativePath( "/valid-authenticated-url/" );
		doReturn( securityContext ).when( factory ).createSecurityContextFor(
				any( HttpServerExchange.class ), any( AuthenticationRule.class ),
				any(SessionStore.class), any(SessionIdManager.class) );
		authenticationHook.handleRequest(exchange);
		verify( authenticationHook ).runAuthenticationInIOThread( eq(exchange), any( AuthenticationRule.class ) );
		assertNotNull( exchange.getSecurityContext() );
	}

	@Test
	@SneakyThrows
	public void ensureThatCallTheHookInSameThreadWhenThereWasRuleThatMatchesTheRelativePath() {
		exchange.setRelativePath( "invalid-authenticated-url/" );
		authenticationHook.handleRequest(exchange);
		verify( authenticationHook, never() ).runAuthenticationInIOThread( eq(exchange), any( AuthenticationRule.class ) );
	}
}
