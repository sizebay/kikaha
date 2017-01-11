package kikaha.uworkers.core;

import kikaha.uworkers.api.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * Unit tests for {@link FailureExchange}.
 */
public class FailureExchangeTest {

    final BiConsumer<Response.UndefinedObject, Throwable> listener = ( r, t ) -> {};
    final IOException failure = new IOException();
    final FailureExchange exchange = new FailureExchange( failure );

    @Test( expected = IllegalStateException.class )
    public void response() throws Exception {
        exchange.response();
    }

    @Test( expected = IllegalStateException.class )
    public void responseAs() throws Exception {
        exchange.responseAs( Object.class );
    }

    @Test( expected = IllegalStateException.class )
    public void then() throws Exception {
        exchange.then( listener );
    }

    @Test( expected = IllegalStateException.class )
    public void request() throws Exception {
        exchange.request();
    }

    @Test( expected = IllegalStateException.class )
    public void requestAs() throws Exception {
        exchange.requestAs( Object.class );
    }

    @Test( expected = IllegalStateException.class )
    public void reply() throws Exception {
        exchange.reply( failure );
    }

    @Test( expected = IllegalStateException.class )
    public void reply1() throws Exception {
        exchange.reply( new Object() );
    }

}