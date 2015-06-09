package kikaha.hazelcast;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.api.SecurityNotification.EventType;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;

import kikaha.core.api.conf.Configuration;
import kikaha.core.auth.AuthenticationRule;
import kikaha.core.impl.conf.DefaultConfiguration;
import kikaha.hazelcast.HazelcastSecurityContextFactory.IncluderOfSessionIntoCacheForAuthenticatedAccounts;
import lombok.val;

import org.junit.Test;
import org.mockito.Mock;

public class SessionManagerShouldBeAbleCacheAuthenticatedSessionsBehaviorTest
	extends AbstractSessionManagerBehaviorTest {

	Configuration configuration = DefaultConfiguration.loadDefaultConfiguration();
	HttpServerExchange exchange = createHttpExchange();

	@Mock
	Account account;

	@Mock
	Principal principal;

	@Mock
	IdentityManager manager;

	AuthenticatedSession session;
	IncluderOfSessionIntoCacheForAuthenticatedAccounts cacheNotifier;

	@Test
	public void ensureFullLifecycleBehavior() {
		treatAnUnauthenticatedRequest();
		resetMocks();
		responseASecondRequestWithCachedData();
	}

	private HttpServerExchange createHttpExchange() {
		final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		final InetSocketAddress address = new InetSocketAddress( "localhost", 8080 );
		exchange.setDestinationAddress( address );
		exchange.setRequestScheme( "http" );
		assertNotNull( exchange.getRequestHeaders() );
		return exchange;
	}

	void treatAnUnauthenticatedRequest() {
		simulateThatHaveNotReceivedCookieFromRequest();
		forceReturnMockedSecurityContext();
		memorizeCacheNotifier();
		factory.createSecurityContextFor( exchange, null );
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
		doReturn( null ).when( sessionCache ).memorize( any( Account.class ), any( HttpServerExchange.class ) );
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
		session = new AuthenticatedSession( SessionID.generateSessionId(), "Chrome", "localhost", SessionAccount.from( account ) );
		doReturn( session ).when( sessionCache ).getSession( any( HttpServerExchange.class ) );
	}

	void forceUseCurrentMockedSessionIdWhileHandlingNotifications() {
		doReturn( this.sessionIdCookie.getValue() ).when( sessionCache ).generateANewId();
	}

	SecurityNotification createAuthenticationNotification() {
		return new SecurityNotification(
			exchange, EventType.AUTHENTICATED, account,
			null, false, null, false );
	}

	private void responseASecondRequestWithCachedData() {
		simulateThatReceivedCookieFromRequest();
		doReturn( chromeSession ).when( sessionCache ).getSession( any( String.class ) );
		simulateAChromeUserAgentRequest();
		val rule = new AuthenticationRule( "", manager,
			new ArrayList<>(), new ArrayList<>(), null, factory, new ArrayList<>() );
		factory.createSecurityContextFor( new HttpServerExchange( null ), rule );
		verify( factory ).preAuthenticatedSecurityContext( any( HttpServerExchange.class ), any( AuthenticatedSession.class ) );
	}
}
