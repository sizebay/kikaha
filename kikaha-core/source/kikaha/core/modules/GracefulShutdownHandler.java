package kikaha.core.modules;

import io.undertow.UndertowMessages;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * Handler that allows for graceful server shutdown. Basically it provides a way to prevent the server from
 * accepting new requests, and wait for existing requests to complete.
 * <p>
 * The handler itself does not shut anything down.
 * <p>
 * Import: The thread safety semantics of the handler are very important. Don't touch anything unless you know
 * what you are doing.
 *
 * <p>
 *     <b>Note</b>: this is a stripped copy from the original {@link io.undertow.server.handlers.GracefulShutdownHandler}, only
 *     minor changes was made.
 * </p>
 *
 * @author Stuart Douglas
 */
@Slf4j
public class GracefulShutdownHandler implements HttpHandler {

    private volatile boolean shutdown = false;
    private final GracefulShutdownListener listener = new GracefulShutdownListener();
    private final Object lock = new Object();

    @Getter(value = AccessLevel.PACKAGE)
    private volatile long activeRequests = 0;
    private static final AtomicLongFieldUpdater<GracefulShutdownHandler> activeRequestsUpdater = AtomicLongFieldUpdater.newUpdater(GracefulShutdownHandler.class, "activeRequests");

    private final HttpHandler next;

    public GracefulShutdownHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (shutdown) {
            exchange.setStatusCode(StatusCodes.SERVICE_UNAVAILABLE);
            exchange.endExchange();
        } else {
            activeRequestsUpdater.incrementAndGet(this);
            exchange.addExchangeCompleteListener(listener);
            next.handleRequest(exchange);
        }
    }

    public void shutdown() {
        shutdown = true;
    }

    /**
     * Waits a set length of time for the handler to shut down
     *
     * @param millis The length of time
     * @return <code>true</code> If the handler successfully shut down
     */
    public boolean awaitShutdown(long millis) throws InterruptedException {
        synchronized (lock) {
            if (!shutdown) {
                throw UndertowMessages.MESSAGES.handlerNotShutdown();
            }
            long end = System.currentTimeMillis() + millis;
            int count = (int) activeRequestsUpdater.get(this);
            while (count != 0) {
                long left = end - System.currentTimeMillis();
                if (left <= 0) {
                    return false;
                }
                lock.wait(left);
                count = (int) activeRequestsUpdater.get(this);
            }
            return true;
        }
    }

    private final class GracefulShutdownListener implements ExchangeCompletionListener {

        @Override
        public void exchangeEvent(HttpServerExchange exchange, NextListener nextListener) {
            try {
                activeRequestsUpdater.decrementAndGet(GracefulShutdownHandler.this);
            } finally {
                nextListener.proceed();
            }
        }
    }
}