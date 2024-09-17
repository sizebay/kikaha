package kikaha.uworkers.core;

import io.undertow.server.*;
import kikaha.urouting.*;
import kikaha.uworkers.api.WorkerRef;
import kikaha.uworkers.local.LocalExchange;
import lombok.RequiredArgsConstructor;

/**
 * Exposes uWorker's endpoint as REST API.
 */
@RequiredArgsConstructor( staticName = "with" )
public class RESTFulMicroWorkersHttpHandler implements HttpHandler {

    final UndertowHelper helper;
    final WorkerRef workerRef;

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if ( exchange.isInIoThread() )
            exchange.dispatch(this);
        else {
            final SimpleExchange simpleExchange = helper.simplify(exchange);
            simpleExchange.receiveRequest((ex, data) -> {
                final LocalExchange localExchange = LocalExchange.create().then(RESTFulUWorkerResponse.to(simpleExchange));
                final RESTFulExchange restFulExchange = new RESTFulExchange(localExchange, simpleExchange, data);
                workerRef.send(restFulExchange);
            });
        }
    }
}
