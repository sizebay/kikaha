package kikaha.core.modules.security;

import java.util.function.Supplier;
import io.undertow.server.HttpServerExchange;

/**
 * Manages the <b>Session Identifier</b> from a given request.
 */
public interface SessionIdManager {

	/**
	 * Attach a new identifier ({@code sessionId}) into the current request ({@code exchange}).
	 *
	 * @param exchange
	 * @param sessionId
	 */
	void attachSessionCookie(HttpServerExchange exchange, String sessionId );

	/**
	 * Extracts the session id from the current request ({@code exchange}).
	 *
	 * @param exchange
	 * @return
	 */
	default String retrieveSessionIdFrom(HttpServerExchange exchange ) {
		return retrieveSessionIdFrom( exchange, this::createNewSessionId );
	}

	/**
	 * Extracts the session id from the current request ({@code exchange}).
	 *
	 * @param exchange
	 * @param sessionIdCreator
	 * @return
	 */
	String retrieveSessionIdFrom(HttpServerExchange exchange, Supplier<String> sessionIdCreator );

	/**
	 * Generate a new session identifier. The default implementation relies on {@link SessionIdGenerator#generate}
	 * implementation.
	 *
	 * @return
	 */
	default String createNewSessionId() {
		return SessionIdGenerator.generate();
	}
}
