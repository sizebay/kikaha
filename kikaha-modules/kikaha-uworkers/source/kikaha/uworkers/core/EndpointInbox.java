package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.api.Worker;
import lombok.*;

/**
 * A supplier is responsible for receiving messages that will be forwarded to
 * a given {@link Worker}. This implementation represents the Worker's Inbox.
 *
 * Created by miere.teixeira on 12/08/2017.
 */
public interface EndpointInbox {

    /**
     * Receive messages. The {@code listener } will be notified with every received message.
     *
     * @param listener
     */
    void receiveMessages( WorkerEndpointMessageListener listener ) throws Exception;

    default void notifyListener(WorkerEndpointMessageListener listener, Exchange exchange) {
        try {
            if ( !EmptyExchange.class.isInstance( exchange ) )
                listener.onMessage(exchange);
        } catch ( final Throwable c ) {
            exchange.reply(c);
        }
    }
}
