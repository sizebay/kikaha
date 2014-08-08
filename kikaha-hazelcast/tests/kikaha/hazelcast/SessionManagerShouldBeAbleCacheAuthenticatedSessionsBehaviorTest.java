package kikaha.hazelcast;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.api.SecurityNotification.EventType;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;

import java.security.Principal;
import java.util.Collections;

import kikaha.hazelcast.SessionManager.IncludeSessionIntoCacheForAuthenticatedAccounts;

import org.junit.Test;
import org.mockito.Mock;

public class SessionManagerShouldBeAbleCacheAuthenticatedSessionsBehaviorTest
	extends AbstractSessionManagerBehaviorTest {

	@Mock
	Account account;

	@Mock
	Principal principal;

	Session session;
	IncludeSessionIntoCacheForAuthenticatedAccounts cacheNotifier;

	@Test
	public void ensureFullLifecycleBehavior() {
		treatAnUnauthenticatedRequest();
		resetMocks();
		responseASecondRequestWithCachedData();
	}

	void treatAnUnauthenticatedRequest() {
		simulateThatHaveNotReceivedCookieFromRequest();
		forceReturnMockedSecurityContext();
		memorizeCacheNotifier();
		sessionManager.createSecurityContextFor( null, null );
		mockAccount();
		forceReturnMockedSessionWhenNotifyingAuthentication();
		cacheNotifier.handleNotification( createAuthenticationNotification() );
		verify( cacheNotifier ).createSessionFrom( eq( account ), any( HttpServerExchange.class ) );
	}

	void memorizeCacheNotifier() {
		cacheNotifier = spy( sessionManager.createReceiverToIncludeSessionIntoCache() );
		doReturn( cacheNotifier ).when( sessionManager ).createReceiverToIncludeSessionIntoCache();
	}

	void forceReturnMockedSessionWhenNotifyingAuthentication() {
		session = new Session( "Generic Browser", "localhost", SessionAccount.from( account ) );
		doReturn( session ).when( cacheNotifier ).createSessionFrom( eq( account ), any( HttpServerExchange.class ) );
		doNothing().when( cacheNotifier ).saveSessionCookieFor( any( HttpServerExchange.class ), anyString() );
	}

	private void mockAccount() {
		doReturn( "User Name" ).when( principal ).getName();
		doReturn( Collections.emptySet() ).when( account ).getRoles();
		doReturn( principal ).when( account ).getPrincipal();
	}

	SecurityNotification createAuthenticationNotification() {
		return new SecurityNotification(
			null, EventType.AUTHENTICATED, account,
			null, false, null, false );
	}

	private void responseASecondRequestWithCachedData() {
		simulateThatReceivedCookieFromRequest();
		fail( "Not finished test." );
	}
}
