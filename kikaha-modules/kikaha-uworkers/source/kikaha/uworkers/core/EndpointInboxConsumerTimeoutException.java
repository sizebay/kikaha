package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;

/**
 * An {@link Exception} that represents a timeout while retrieving an {@link Exchange}.
 */
public class EndpointInboxConsumerTimeoutException extends RuntimeException {

    public EndpointInboxConsumerTimeoutException(final String message, final Throwable cause ) {
        super( message, cause );
    }

    public EndpointInboxConsumerTimeoutException(final String message ) {
        super( message );
    }

    public EndpointInboxConsumerTimeoutException(final Throwable cause ) {
        super( cause );
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
