package kikaha.core.auth;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.api.SecurityNotification.EventType;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import lombok.val;

public class BasicAuthenticationNotificationReceiver implements NotificationReceiver {

	@Override
	public void handleNotification( final SecurityNotification notification ) {
		val exchange = notification.getExchange();
		if ( notification.getEventType().equals( EventType.FAILED_AUTHENTICATION ) )
			sendAuthenticationRequiredResponse( exchange );
	}

	private void sendAuthenticationRequiredResponse( final HttpServerExchange exchange ) {
		exchange.setResponseCode( 401 );
		val wwwAuthenticateHeader = new HttpString( "WWW-Authenticate" );
		exchange.getResponseHeaders().add( wwwAuthenticateHeader, "Basic realm=\"default\"" );
		exchange.endExchange();
	}
}
