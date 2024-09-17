package kikaha.cloud.smart.tracer;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import kikaha.cloud.smart.tracer.TraceId;
import kikaha.cloud.smart.tracer.TraceIdContextProducer;
import kikaha.core.modules.security.SessionIdGenerator;
import kikaha.core.test.HttpServerExchangeStub;
import kikaha.urouting.api.RoutingException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link TraceIdContextProducer}.
 * Created by ibratan on 16/06/2017.
 */
public class TraceIdContextProducerTest {

    static final HttpString TRACE_ID_HEADER = new HttpString("X-TraceId");
    static final String GENERATED_ID = SessionIdGenerator.generate();

    final TraceIdContextProducer producer = new TraceIdContextProducer( TRACE_ID_HEADER );

    @Test
    public void canRetrieveTraceIdFromRequest() throws Exception {
        final HttpServerExchange httpExchange = HttpServerExchangeStub.createHttpExchange();
        httpExchange.getResponseHeaders().put( TRACE_ID_HEADER, GENERATED_ID );
        final TraceId traceId = producer.produce(httpExchange);
        assertNotNull( traceId );
        assertTrue( traceId.isPresent() );
        assertEquals(GENERATED_ID, traceId.id);
    }

    @Test
    public void willFailIfNoHeaderWasDefined() throws RoutingException {
        final HttpServerExchange httpExchange = HttpServerExchangeStub.createHttpExchange();
        final TraceId traceId = producer.produce(httpExchange);
        assertFalse( traceId.isPresent() );
    }
}