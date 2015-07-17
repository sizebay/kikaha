package kikaha.core.auth;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.api.SecurityNotification.EventType;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import trip.spi.Singleton;

@Singleton
public class SessionCleanerNotificationReceiver implements NotificationReceiver {

	@Override
	public void handleNotification(SecurityNotification notification) {
		if ( notification.getEventType().equals( EventType.LOGGED_OUT ) ) {
			final HttpServerExchange exchange = notification.getExchange();
			final SessionManager sessionManager = exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
			final SessionConfig sessionConfig = exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
			final Session session = sessionManager.getSession( exchange, sessionConfig);
			session.invalidate(exchange);
		}
	}
}
