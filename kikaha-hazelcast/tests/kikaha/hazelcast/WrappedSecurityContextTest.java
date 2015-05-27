package kikaha.hazelcast;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import kikaha.hazelcast.AuthenticationEventInterceptor;
import kikaha.hazelcast.WrappedSecurityContext;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Many tests described here is just ensuring that the delegating process are
 * working well, once the {@code Delegate} annotation from lombok becomes
 * deprecated recently.
 *
 * @author Miere Teixeira
 */
@RunWith( MockitoJUnitRunner.class )
public class WrappedSecurityContextTest {

	@Mock
	SecurityContext mockedContext;

	@Mock
	AuthenticationMechanism mechanism;

	@Mock
	Account account;

	@Mock
	NotificationReceiver receiver;

	@Mock
	AuthenticationEventInterceptor interceptorBeforeAuthenticate;

	WrappedSecurityContext context;

	@Before
	public void setup() {
		context = new WrappedSecurityContext( mockedContext );
	}

	@Test
	public void ensureThatDelegatedMethods() {
		context.notifyBeforeAuthenticate( interceptorBeforeAuthenticate );
		runAllPublicMethodsOfWrappedSecurityContext();
		ensureHaveDelegatedAllPublicMethodsToMockedContext();
		verify( interceptorBeforeAuthenticate ).intercep();
	}

	@SuppressWarnings("deprecation")
	private void runAllPublicMethodsOfWrappedSecurityContext() {
		context.addAuthenticationMechanism( mechanism );
		context.authenticate();
		context.authenticationComplete( account, "mechanismName", false );
		context.authenticationFailed( "failed", "mechanismName" );
		context.getAuthenticatedAccount();
		context.getAuthenticationMechanisms();
		context.getIdentityManager();
		context.getMechanismName();
		context.isAuthenticated();
		context.isAuthenticationRequired();
		context.login( "username", "password" );
		context.logout();
		context.registerNotificationReceiver( receiver );
		context.removeNotificationReceiver( receiver );
		context.setAuthenticationRequired();
	}

	@SuppressWarnings("deprecation")
	private void ensureHaveDelegatedAllPublicMethodsToMockedContext() {
		verify( mockedContext ).addAuthenticationMechanism( eq( mechanism ) );
		verify( mockedContext ).authenticate();
		verify( mockedContext ).authenticationComplete( eq( account ), eq( "mechanismName" ), eq( false ) );
		verify( mockedContext ).authenticationFailed( eq( "failed" ), eq( "mechanismName" ) );
		verify( mockedContext ).getAuthenticatedAccount();
		verify( mockedContext ).getAuthenticationMechanisms();
		verify( mockedContext ).getIdentityManager();
		verify( mockedContext ).getMechanismName();
		verify( mockedContext ).isAuthenticated();
		verify( mockedContext ).isAuthenticationRequired();
		verify( mockedContext ).login( eq( "username" ), eq( "password" ) );
		verify( mockedContext ).logout();
		verify( mockedContext ).registerNotificationReceiver( eq( receiver ) );
		verify( mockedContext ).removeNotificationReceiver( eq( receiver ) );
		verify( mockedContext ).setAuthenticationRequired();
	}
}