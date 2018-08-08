package kikaha.uworkers.core;

import kikaha.uworkers.api.*;
import org.slf4j.*;

/**
 * A supplier is responsible for receiving messages that will be forwarded to
 * a given {@link Worker}. This implementation represents the Worker's Inbox.
 *
 * Created by miere.teixeira on 12/08/2017.
 */
public interface EndpointInbox {

    Logger _EndpointInboxLogger = LoggerFactory.getLogger( EndpointInbox.class );

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
            if ( exchange.isReplySupported() )
                exchange.reply(c);
            else _EndpointInboxLogger.error( "Failed to notify the listener", c );
        }
    }
}
