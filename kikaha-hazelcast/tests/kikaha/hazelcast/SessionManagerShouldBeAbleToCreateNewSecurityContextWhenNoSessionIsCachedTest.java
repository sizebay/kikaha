package kikaha.hazelcast;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpServerExchange;
import kikaha.core.auth.AuthenticationRule;
import kikaha.hazelcast.HazelcastSecurityContextFactory.IncludeSessionIntoCacheForAuthenticatedAccounts;
import kikaha.hazelcast.HazelcastSecurityContextFactory.RemoveSessionFromCacheForLoggedOutAccounts;

import org.junit.Test;

public class SessionManagerShouldBeAbleToCreateNewSecurityContextWhenNoSessionIsCachedTest
	extends AbstractSessionManagerBehaviorTest {

	@Test
	public void ensureThatIdentifiedCookieFromRequestAndCreateANewSecurityContextWhenNoSessionCachedFound() {
		simulateThatReceivedCookieFromRequest();
		simulateAChromeUserAgentRequest();
		forceReturnMockedSecurityContext();
		assertSame( securityContext, factory.createSecurityContextFor( new HttpServerExchange( null ), null ) );
		verify( factory ).postAuthenticatedSecurityContext( any( HttpServerExchange.class ), any( AuthenticationRule.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( IncludeSessionIntoCacheForAuthenticatedAccounts.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( RemoveSessionFromCacheForLoggedOutAccounts.class ) );
	}

	@Test
	public void ensureThatHaveNoCookieAndCreateANewSecurityContextWhenNoSessionCachedFound() {
		simulateThatHaveNotReceivedCookieFromRequest();
		simulateAChromeUserAgentRequest();
		forceReturnMockedSecurityContext();
		assertSame( securityContext, factory.createSecurityContextFor( new HttpServerExchange( null ), null ) );
		verify( factory ).postAuthenticatedSecurityContext( any( HttpServerExchange.class ), any( AuthenticationRule.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( IncludeSessionIntoCacheForAuthenticatedAccounts.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( RemoveSessionFromCacheForLoggedOutAccounts.class ) );
	}
}
