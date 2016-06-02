package kikaha.core.modules.security;

import io.undertow.server.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * An {@link ExchangeCompletionListener} that flushes a {@link SecurityContext}.
 */
@Slf4j
@RequiredArgsConstructor
public class SecurityContextAutoUpdater implements ExchangeCompletionListener {

	final SecurityContext securityContext;

	@Override
	public void exchangeEvent(HttpServerExchange exchange, NextListener nextListener) {
		try {
			securityContext.updateCurrentSession();
		// it should handle any exceptions here...
		} catch ( Throwable cause ) {
			log.error( "Can't update the current session: " + cause.getMessage(), cause );
		} finally {
			nextListener.proceed();
		}
	}
}
