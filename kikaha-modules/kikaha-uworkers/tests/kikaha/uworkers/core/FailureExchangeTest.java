package kikaha.uworkers.core;

import org.junit.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;

/**
 * Unit tests for {@link FailureExchange}.
 */
public class FailureExchangeTest {

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
        exchange.then( any() );
    }

    @Test( expected = IllegalStateException.class )
    public void request() throws Exception {
        exchange.request();
    }

    @Test( expected = IllegalStateException.class )
    public void requestAs() throws Exception {
        exchange.requestAs( any() );
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