package kikaha.cloud.smart.tracer;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import kikaha.core.modules.security.SessionIdGenerator;
import lombok.RequiredArgsConstructor;

/**
 * Created by ibratan on 16/06/2017.
 */
@RequiredArgsConstructor
public class TraceIdHttpHandler implements HttpHandler {

    final HttpString TRACE_ID_HEADER;
    final HttpHandler nextHandler;

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String traceId = exchange.getRequestHeaders().getFirst(TRACE_ID_HEADER);
        if ( traceId == null )
            traceId = SessionIdGenerator.generate();
        exchange.getResponseHeaders().put(TRACE_ID_HEADER, traceId );
        nextHandler.handleRequest( exchange );
    }
}
