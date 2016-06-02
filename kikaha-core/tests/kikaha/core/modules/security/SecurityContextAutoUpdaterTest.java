package kikaha.core.modules.security;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import io.undertow.server.ExchangeCompletionListener.NextListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link SecurityContextAutoUpdater}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityContextAutoUpdaterTest {

	@Mock NextListener listener;
	@Mock SecurityContext securityContext;

	@Test
	public void ensureThatFlushesTheSecurityContext(){
		final SecurityContextAutoUpdater updater = new SecurityContextAutoUpdater(securityContext);
		updater.exchangeEvent( null, listener );
		verify( securityContext ).updateCurrentSession();
	}

	@Test
	public void ensureThatCallsTheNextListener(){
		final SecurityContextAutoUpdater updater = new SecurityContextAutoUpdater(securityContext);
		updater.exchangeEvent( null, listener );
		verify( listener ).proceed();
	}

	@Test
	public void ensureThatCallsTheNextListenerEvenIfTheSecurityContextFlushMethodFails(){
		doThrow( RuntimeException.class ).when(securityContext).updateCurrentSession();
		final SecurityContextAutoUpdater updater = new SecurityContextAutoUpdater(securityContext);
		updater.exchangeEvent( null, listener );
		verify( listener ).proceed();
	}
}
