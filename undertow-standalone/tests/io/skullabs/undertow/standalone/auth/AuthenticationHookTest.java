package io.skullabs.undertow.standalone.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.skullabs.undertow.standalone.DefaultConfiguration;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.server.HttpServerExchange;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthenticationHookTest {

	@Mock
	RequestHookChain chain;
	AuthenticationHook authenticationHook;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		val config = DefaultConfiguration.loadDefaultConfiguration();
		val authenticationRuleMatcher = new AuthenticationRuleMatcher( config.authentication() );
		authenticationHook = spy( new AuthenticationHook( authenticationRuleMatcher ) );
	}

	@Test
	public void ensureThatCallTheHookInIOThreadWhenHasRuleThatMatchesTheRelativePath() throws UndertowStandaloneException {
		doNothing().when( chain ).executeInIOThread( any( Runnable.class ) );
		doReturn( "/valid-authenticated-url/" ).when( authenticationHook ).retrieveRelativePath( any( HttpServerExchange.class ) );
		authenticationHook.execute( chain, null );
		verify( chain ).executeInIOThread( any( Runnable.class ) );
	}

	@Test
	public void ensureThatCallTheHookInSameThreadWhenThereWasRuleThatMatchesTheRelativePath() throws UndertowStandaloneException {
		doReturn( "invalid-authenticated-url/" ).when( authenticationHook ).retrieveRelativePath( any( HttpServerExchange.class ) );
		authenticationHook.execute( chain, null );
		verify( chain, never() ).executeInIOThread( any( Runnable.class ) );
	}
}
