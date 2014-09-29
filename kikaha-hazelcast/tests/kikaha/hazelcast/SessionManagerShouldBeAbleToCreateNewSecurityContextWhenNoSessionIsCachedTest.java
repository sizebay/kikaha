package kikaha.hazelcast;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpServerExchange;
import kikaha.core.auth.AuthenticationRule;
import kikaha.hazelcast.HazelcastSecurityContextFactory.IncluderOfSessionIntoCacheForAuthenticatedAccounts;
import kikaha.hazelcast.HazelcastSecurityContextFactory.RemoveSessionFromCacheForLoggedOutAccounts;

import org.junit.Test;

public class SessionManagerShouldBeAbleToCreateNewSecurityContextWhenNoSessionIsCachedTest
	extends AbstractSessionManagerBehaviorTest {

	@Test
	public void ensureThatIdentifiedCookieFromRequestAndCreateANewSecurityContextWhenNoSessionCachedFound() {
		simulateThatReceivedCookieFromRequest();
		forceReturnMockedSecurityContext();
		assertSame( wrappedSecurityContext, factory.createSecurityContextFor( new HttpServerExchange( null ), null ) );
		verify( factory ).postAuthenticatedSecurityContext( any( HttpServerExchange.class ), any( AuthenticatedSession.class ),
			any( AuthenticationRule.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( IncluderOfSessionIntoCacheForAuthenticatedAccounts.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( RemoveSessionFromCacheForLoggedOutAccounts.class ) );
	}

	@Test
	public void ensureThatHaveNoCookieAndCreateANewSecurityContextWhenNoSessionCachedFound() {
		simulateThatHaveNotReceivedCookieFromRequest();
		forceReturnMockedSecurityContext();
		assertSame( wrappedSecurityContext, factory.createSecurityContextFor( new HttpServerExchange( null ), null ) );
		verify( factory ).postAuthenticatedSecurityContext( any( HttpServerExchange.class ), any( AuthenticatedSession.class ),
			any( AuthenticationRule.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( IncluderOfSessionIntoCacheForAuthenticatedAccounts.class ) );
		verify( securityContext ).registerNotificationReceiver( isA( RemoveSessionFromCacheForLoggedOutAccounts.class ) );
	}
}
