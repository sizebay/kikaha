package kikaha.cloud.smart.tracer;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import kikaha.cloud.smart.tracer.TraceIdHttpHandler;
import kikaha.core.modules.security.SessionIdGenerator;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TraceIdHttpHandler}.
 * Created by ibratan on 16/06/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TraceIdHttpHandlerTest {

    static final HttpString TRACE_ID_HEADER = new HttpString("X-TraceId");
    static final String GENERATED_ID = SessionIdGenerator.generate();

    @Mock HttpHandler nextHandler;
    TraceIdHttpHandler handler;

    @Before
    public void configureTraceIdHttpHandler(){
        handler = new TraceIdHttpHandler( TRACE_ID_HEADER, nextHandler );
    }

    @Test
    public void ensureCanCreateAndAttachANewTraceId() throws Exception {
        final HttpServerExchange httpExchange = HttpServerExchangeStub.createHttpExchange();
        handler.handleRequest( httpExchange );
        final String traceId = httpExchange.getResponseHeaders().getFirst(TRACE_ID_HEADER);
        assertNotNull( traceId );
        verify( nextHandler ).handleRequest( eq(httpExchange) );
    }

    @Test
    public void ensureCanAttachTraceIdFoundOnRequest() throws Exception {
        final HttpServerExchange httpExchange = HttpServerExchangeStub.createHttpExchange();
        httpExchange.getRequestHeaders().put( TRACE_ID_HEADER, GENERATED_ID );
        handler.handleRequest( httpExchange );
        final String traceId = httpExchange.getResponseHeaders().getFirst( TRACE_ID_HEADER );
        assertNotNull( traceId );
        assertEquals( GENERATED_ID, traceId );
        verify( nextHandler ).handleRequest( eq(httpExchange) );
    }
}