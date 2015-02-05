package kikaha.core.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.KikahaException;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import trip.spi.ServiceProvider;

public class AuthenticationHookTest {

	@Mock
	SecurityContext securityContext;

	@Mock
	RequestHookChain chain;
	AuthenticationHook authenticationHook;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		val config = DefaultConfiguration.loadDefaultConfiguration();
		val provider = new ServiceProvider();
		val authenticationRuleMatcher = new AuthenticationRuleMatcher( provider, config.authentication() );
		authenticationHook = spy( new AuthenticationHook( authenticationRuleMatcher, config ) );
	}

	@Test
	public void ensureThatCallTheHookInIOThreadWhenHasRuleThatMatchesTheRelativePath() throws KikahaException {
		doNothing().when( chain ).executeInWorkerThread( any( Runnable.class ) );
		doReturn( securityContext ).when( authenticationHook ).createSecurityContext( any( HttpServerExchange.class ),
				any( AuthenticationRule.class ) );
		doReturn( "/valid-authenticated-url/" ).when( authenticationHook ).retrieveRelativePath( any( HttpServerExchange.class ) );
		authenticationHook.execute( chain, null );
		verify( chain ).executeInWorkerThread( any( Runnable.class ) );
	}

	@Test
	public void ensureThatCallTheHookInSameThreadWhenThereWasRuleThatMatchesTheRelativePath() throws KikahaException {
		doReturn( "invalid-authenticated-url/" ).when( authenticationHook ).retrieveRelativePath( any( HttpServerExchange.class ) );
		authenticationHook.execute( chain, null );
		verify( chain, never() ).executeInWorkerThread( any( Runnable.class ) );
	}
}
