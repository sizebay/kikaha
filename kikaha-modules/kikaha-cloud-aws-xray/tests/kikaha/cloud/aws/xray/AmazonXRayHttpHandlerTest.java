package kikaha.cloud.aws.xray;

import static kikaha.core.test.Exposed.expose;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import java.net.InetSocketAddress;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.strategy.sampling.SamplingStrategy;
import io.undertow.server.*;
import io.undertow.util.Headers;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonXRayHttpHandlerTest {

	HttpServerExchange httpExchange = createRequest();

	@Mock SamplingStrategy samplingStrategy;
	@Spy AWSXRayRecorder recorder;
	@Mock SegmentNamingStrategy namingStrategy;
	@Mock HttpHandler handler;
	@Mock Segment segment;
	@Mock ApplicationData applicationData;

	@InjectMocks
	@Spy SegmentFactory segmentFactory;

	@Before
	public void configureMocks(){
		doReturn( samplingStrategy ).when( recorder ).getSamplingStrategy();
		doAnswer( c-> c.getArgumentAt(0, String.class) ).when( namingStrategy ).nameForRequest( anyString() );
		doReturn( segment ).when( segmentFactory ).beginDummySegment( anyString(), any() );
		doReturn( true ).when( segment ).end();
		doReturn( "sample.app.local" ).when( applicationData ).getName();
	}

	@Test
	public void ensureCanExecuteRequest() throws Exception {
		final AmazonXRayHttpHandler amazonXRayHttpHandler = new AmazonXRayHttpHandler(recorder, segmentFactory, handler);
		amazonXRayHttpHandler.handleRequest( httpExchange );
		httpExchange.endExchange();

		verify( handler ).handleRequest( eq(httpExchange) );
		//verify( recorder ).sendSegment( eq(segment) );
	}

	@Test
	public void ensureCanHandleExecutionException() throws Exception {
		doThrow( new UnsupportedOperationException() ).when( handler ).handleRequest( eq(httpExchange) );

		try {
			final AmazonXRayHttpHandler amazonXRayHttpHandler = new AmazonXRayHttpHandler(recorder, segmentFactory, handler);
			amazonXRayHttpHandler.handleRequest(httpExchange);
			fail("It was expecting to throw an exception");
		} catch ( UnsupportedOperationException cause ) {
			verify( segment ).addException( eq(cause) );
		}
	}

	HttpServerExchange createRequest(){
		final HttpServerExchange httpExchange = HttpServerExchangeStub.createHttpExchange();
		expose( httpExchange ).setFieldValue( "destinationAddress", new InetSocketAddress("localhost", 80) );
		httpExchange.getRequestHeaders().put( Headers.HOST, "localhost" );
		return httpExchange;
	}
}
