package kikaha.core.modules.security;

import io.undertow.server.HttpServerExchange;

/**
 * Listen to events regarding the Authentication/Authorization life cycle.
 */
public interface SecurityEventListener {

    void onEvent(SecurityEventType type, HttpServerExchange exchange, Session session );

    enum SecurityEventType {
        LOGIN, LOGOUT, PROFILE_UPDATED
    }
}
