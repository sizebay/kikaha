package kikaha.hazelcast;

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

import kikaha.hazelcast.HazelcastSecurityContextFactory.IncludeSessionIntoCacheForAuthenticatedAccounts;

import org.junit.Test;
import org.mockito.Mock;

public class SessionManagerShouldBeAbleCacheAuthenticatedSessionsBehaviorTest
	extends AbstractSessionManagerBehaviorTest {

	@Mock
	Account account;

	@Mock
	Principal principal;

	AuthenticatedSession session;
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
		factory.createSecurityContextFor( new HttpServerExchange( null ), null );
		simulateAsynNotificationWhenAuthenticationIsSuccessful();
	}

	void memorizeCacheNotifier() {
		cacheNotifier = spy( factory.createReceiverToIncludeSessionIntoCache() );
		doReturn( cacheNotifier ).when( factory ).createReceiverToIncludeSessionIntoCache();
	}

	void simulateAsynNotificationWhenAuthenticationIsSuccessful() {
		mockAccount();
		forceReturnMockedSessionWhenNotifyingAuthentication();
		forceUseCurrentMockedSessionIdWhileHandlingNotifications();
		cacheNotifier.handleNotification( createAuthenticationNotification() );
		verify( sessionCache ).memorize( any( Account.class ), any( HttpServerExchange.class ) );
		// verify( cacheNotifier ).createSessionFrom( eq( account ), any(
		// HttpServerExchange.class ) );
		// verify( cache ).put( eq( sessionIdCookie.getValue() ), any(
		// AuthenticatedSession.class ) );
	}

	void mockAccount() {
		doReturn( "User Name" ).when( principal ).getName();
		doReturn( Collections.emptySet() ).when( account ).getRoles();
		doReturn( principal ).when( account ).getPrincipal();
	}

	void forceReturnMockedSessionWhenNotifyingAuthentication() {
		session = new AuthenticatedSession( null, "Chrome", "localhost", SessionAccount.from( account ) );
		doReturn( session ).when( sessionCache ).memorize( eq( account ), any( HttpServerExchange.class ) );
		doNothing().when( sessionCache ).saveSessionCookieFor( any( HttpServerExchange.class ), anyString() );
	}

	void forceUseCurrentMockedSessionIdWhileHandlingNotifications() {
		doReturn( this.sessionIdCookie.getValue() ).when( sessionCache ).generateANewId();
	}

	SecurityNotification createAuthenticationNotification() {
		return new SecurityNotification(
			null, EventType.AUTHENTICATED, account,
			null, false, null, false );
	}

	private void responseASecondRequestWithCachedData() {
		simulateThatReceivedCookieFromRequest();
		doReturn( chromeSession ).when( sessionCache ).getSession( any( String.class ) );
		simulateAChromeUserAgentRequest();
		factory.createSecurityContextFor( new HttpServerExchange( null ), null );
		verify( factory ).preAuthenticatedSecurityContext( any( HttpServerExchange.class ), any( AuthenticatedSession.class ) );
	}
}
