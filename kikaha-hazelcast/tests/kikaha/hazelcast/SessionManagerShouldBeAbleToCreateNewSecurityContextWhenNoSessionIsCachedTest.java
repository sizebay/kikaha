package kikaha.hazelcast;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpServerExchange;
import kikaha.core.auth.AuthenticationRule;
import kikaha.hazelcast.SessionManager.IncludeSessionIntoCacheForAuthenticatedAccounts;
import kikaha.hazelcast.SessionManager.RemoveSessionFromCacheForLoggedOutAccounts;

import org.junit.Test;

public class SessionManagerShouldBeAbleToCreateNewSecurityContextWhenNoSessionIsCachedTest
	extends AbstractSessionManagerBehaviorTest {

	@Test
	public void ensureThatIdentifiedCookieFromRequestAndCreateANewSecurityContextWhenNoSessionCachedFound() {
		simulateThatReceivedCookieFromRequest();
		forceReturnMockedSecurityContext();
		assertSame( securityContext, sessionManager.createSecurityContextFor( null, null ) );
		verify( sessionManager ).postAuthenticatedSecurityContext( any( HttpServerExchange.class ), any( AuthenticationRule.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( IncludeSessionIntoCacheForAuthenticatedAccounts.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( RemoveSessionFromCacheForLoggedOutAccounts.class ) );
	}

	@Test
	public void ensureThatHaveNoCookieAndCreateANewSecurityContextWhenNoSessionCachedFound() {
		simulateThatHaveNotReceivedCookieFromRequest();
		forceReturnMockedSecurityContext();
		assertSame( securityContext, sessionManager.createSecurityContextFor( null, null ) );
		verify( sessionManager ).postAuthenticatedSecurityContext( any( HttpServerExchange.class ), any( AuthenticationRule.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( IncludeSessionIntoCacheForAuthenticatedAccounts.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( RemoveSessionFromCacheForLoggedOutAccounts.class ) );
	}
}
