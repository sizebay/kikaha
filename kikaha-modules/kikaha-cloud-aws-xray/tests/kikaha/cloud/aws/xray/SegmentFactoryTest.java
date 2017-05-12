package kikaha.cloud.aws.xray;

import static kikaha.core.test.Exposed.expose;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import java.net.InetSocketAddress;
import java.util.Map;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.entities.*;
import com.amazonaws.xray.strategy.sampling.SamplingStrategy;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import kikaha.core.test.HttpServerExchangeStub;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SegmentFactoryTest {

	HttpServerExchange httpExchange = createRequest();

	@Mock SamplingStrategy samplingStrategy;
	@Spy AWSXRayRecorder recorder;
	@Mock SegmentNamingStrategy namingStrategy;

	@InjectMocks
	@Spy SegmentFactory segmentFactory;

	@Before
	public void configureMocks(){
		doReturn( samplingStrategy ).when( recorder ).getSamplingStrategy();
		doAnswer( c-> c.getArgumentAt(0, String.class) ).when( namingStrategy ).nameForRequest( anyString() );
	}

	@Test
	public void ensureCanCreateDummySegment() throws Exception {
		final Segment segment = segmentFactory.createSegment(httpExchange);
		assertNotNull( segment );
		assertEquals( "localhost", segment.getName() );
	}

	@Test
	public void ensureCanCreateSegment() throws Exception {
		doReturn(true).when(samplingStrategy).shouldTrace( anyString(), anyString(), anyString() );

		final Segment segment = segmentFactory.createSegment(httpExchange);
		assertNotNull( segment );
		assertEquals( "localhost", segment.getName() );
	}

	@Test
	public void ensureSegmentHaveAllExpectedAttributes() throws Exception {
		final Segment segment = segmentFactory.createSegment(httpExchange);
		final Map<String, Object> attributes = (Map<String, Object>) segment.getHttp().get("request");
		assertNotNull( attributes );
		assertEquals( "localhost", attributes.get( "client_ip" ) );
		assertEquals( "/test", attributes.get( "url" ) );
		assertEquals( "GET", attributes.get( "method" ) );
	}

	HttpServerExchange createRequest(){
		final HttpServerExchange httpExchange = HttpServerExchangeStub.createHttpExchange();
		expose( httpExchange ).setFieldValue( "destinationAddress", new InetSocketAddress("localhost", 80) );
		httpExchange.getRequestHeaders().put( Headers.HOST, "localhost" );
		return httpExchange;
	}
}