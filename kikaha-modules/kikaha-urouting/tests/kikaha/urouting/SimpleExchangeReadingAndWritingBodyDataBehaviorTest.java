package kikaha.urouting;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.io.IOException;
import io.undertow.server.HttpServerExchange;
import kikaha.core.test.HttpServerExchangeStub;
import kikaha.urouting.api.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleExchangeReadingAndWritingBodyDataBehaviorTest {

	private static final String CONTENT_TYPE = "any-content-type";

	@Spy
	RoutingMethodParameterReader parameterReader;

	@Mock
	SerializerAndUnserializerProvider serializerAndUnserializerProvider;

	@Mock
	Unserializer unserializer;

	@Mock
	Serializer serializer;

	@Spy
	RoutingMethodResponseWriter responseWriter;

	@Before
	public void configureRequestReaderAndResponseWriter() throws IOException {
		doReturn( unserializer ).when( serializerAndUnserializerProvider ).getUnserializerFor( eq(CONTENT_TYPE) );
		parameterReader.serializerAndUnserializerProvider = serializerAndUnserializerProvider;
		doReturn( serializer ).when( serializerAndUnserializerProvider ).getSerializerFor( eq(CONTENT_TYPE) );
		responseWriter.serializerAndUnserializerProvider = serializerAndUnserializerProvider;
	}

	@Test
	public void ensureThatIsAbleToReadAllPostData() throws IOException {
		final HttpServerExchange request = createExchange();
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		doReturn( "Hello World" ).when( unserializer ).unserialize( eq(request), eq( String.class), anyString() );
		assertEquals( "Hello World", exchange.getRequestBody( String.class, CONTENT_TYPE ) );
	}

	@Test
	public void ensureThatIsAbleToSendResponse() throws IOException {
		final HttpServerExchange request = createExchange();
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		exchange.sendResponse( DefaultResponse.ok( "Hello World" ).contentType( CONTENT_TYPE ) );
		verify( serializer ).serialize( eq("Hello World"), eq(request), anyString() );
	}

	@Test
	public void ensureThatIsAbleToSendResponseWithRawObject() throws IOException {
		final HttpServerExchange request = createExchange();
		final SimpleExchange exchange = SimpleExchange.wrap( request, parameterReader, responseWriter );
		exchange.sendResponse("Hello World", CONTENT_TYPE);
		verify( serializer ).serialize( eq("Hello World"), eq(request), anyString() );
	}

	private HttpServerExchange createExchange() {
		return HttpServerExchangeStub.createHttpExchange();
	}
}