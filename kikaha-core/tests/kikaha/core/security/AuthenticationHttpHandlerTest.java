package kikaha.core.security;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.HttpServerExchangeStub;
import kikaha.core.api.KikahaException;
import kikaha.core.impl.conf.DefaultConfiguration;
import kikaha.core.security.AuthenticationHttpHandler;
import kikaha.core.security.AuthenticationRule;
import kikaha.core.security.AuthenticationRuleMatcher;
import kikaha.core.security.SecurityContextFactory;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import trip.spi.DefaultServiceProvider;

public class AuthenticationHttpHandlerTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Mock
	SecurityContext securityContext;

	@Mock
	HttpHandler rootHandler;

	@Mock
	SecurityContextFactory factory;

	AuthenticationHttpHandler authenticationHook;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		val config = DefaultConfiguration.loadDefaultConfiguration();
		val provider = new DefaultServiceProvider();
		val authenticationRuleMatcher = new AuthenticationRuleMatcher( provider, config.authentication() );
		authenticationHook = spy( new AuthenticationHttpHandler( authenticationRuleMatcher, config, rootHandler, factory ) );
	}

	@Test
	@SneakyThrows
	public void ensureThatCallTheHookInIOThreadWhenHasRuleThatMatchesTheRelativePath() throws KikahaException {
		doNothing().when(authenticationHook).runAuthenticationInIOThread( any(), any(), any());
		exchange.setRelativePath( "/valid-authenticated-url/" );
		doReturn( securityContext ).when( factory ).createSecurityContextFor( any( HttpServerExchange.class ),
				any( AuthenticationRule.class ) );
		authenticationHook.handleRequest(exchange);
		verify( authenticationHook ).runAuthenticationInIOThread( eq(exchange), any( AuthenticationRule.class ) );
	}

	@Test
	@SneakyThrows
	public void ensureThatCallTheHookInSameThreadWhenThereWasRuleThatMatchesTheRelativePath() throws KikahaException {
		exchange.setRelativePath( "invalid-authenticated-url/" );
		authenticationHook.handleRequest(exchange);
		verify( authenticationHook, never() ).runAuthenticationInIOThread( eq(exchange), any( AuthenticationRule.class ) );
	}
}
