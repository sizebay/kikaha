package kikaha.core.modules.undertow;

import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.RequiredArgsConstructor;

/**
 * Created by miere.teixeira on 12/06/2017.
 */
@RequiredArgsConstructor(staticName = "response")
public class BodyResponseSender implements DefaultResponseListener {

    final int status;
    final String contentType;
    final String response;

    public static void response(HttpServerExchange exchange, int status, final String contentType, final String response) {
        exchange.addDefaultResponseListener( BodyResponseSender.response(status, contentType, response) );
        exchange.endExchange();
    }

    @Override
    public boolean handleDefaultResponse(HttpServerExchange exchange) {
        exchange.setStatusCode( status );
        exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, contentType );
        exchange.getResponseSender().send(response);
        exchange.endExchange();
        return true;
    }
}
