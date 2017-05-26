package kikaha.core.modules.security;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import javax.inject.Inject;
import io.undertow.server.*;
import kikaha.config.Config;
import kikaha.core.cdi.CDI;
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

	@Inject SecurityConfiguration securityConfiguration;
	@Inject CDI provider;
	@Inject Config config;
	@Inject DefaultAuthenticationConfiguration defaultAuthenticationConfiguration;

	AuthenticationHttpHandler authenticationHook;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		AuthenticationRuleMatcher authenticationRuleMatcher = new AuthenticationRuleMatcher( provider, config.getConfig("server.auth"), defaultAuthenticationConfiguration);
		authenticationHook = spy( new AuthenticationHttpHandler(
				authenticationRuleMatcher, defaultAuthenticationConfiguration.getPermissionDeniedPage(),
				rootHandler, securityConfiguration ) );
		securityConfiguration.setFactory( factory );
	}

	@Test
	@SneakyThrows
	public void ensureThatCallTheHookInIOThreadWhenHasRuleThatMatchesTheRelativePath() {
		doNothing().when(authenticationHook).runAuthenticationInIOThread( any(), any(), any());
		securityConfiguration.setRequestMatcherIfAbsent( e -> true );
		exchange.setRelativePath( "/valid-authenticated-url/" );
		doReturn( securityContext ).when( factory ).createSecurityContextFor(
				eq(exchange), any( AuthenticationRule.class ), eq(securityConfiguration) );
		authenticationHook.handleRequest(exchange);
		verify( authenticationHook ).runAuthenticationInIOThread( eq(exchange), any( AuthenticationRule.class ), eq(securityContext) );
		assertNotNull( exchange.getSecurityContext() );
	}

	@Test
	@SneakyThrows
	public void ensureThatCallTheHookInSameThreadWhenThereWasRuleThatMatchesTheRelativePath() {
		doNothing().when(authenticationHook).runAuthenticationInIOThread( any(), any(), any());
		doReturn(securityContext).when(factory).createSecurityContextFor(
				eq(exchange), any( AuthenticationRule.class ), eq( securityConfiguration ) );
		exchange.setRelativePath( "invalid-authenticated-url/" );
		authenticationHook.handleRequest(exchange);
		verify( authenticationHook ).runAuthenticationInIOThread( eq(exchange), eq(AuthenticationRule.EMPTY), eq(securityContext) );
		assertNotNull( exchange.getSecurityContext() );
	}
}
