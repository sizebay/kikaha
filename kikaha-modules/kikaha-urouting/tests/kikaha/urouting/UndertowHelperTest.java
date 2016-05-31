package kikaha.urouting;

import static org.junit.Assert.assertEquals;
import io.undertow.server.HttpServerExchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UndertowHelperTest {

	@Mock RoutingMethodParameterReader parameterReader;
	@Mock RoutingMethodResponseWriter responseWriter;

	@Test
	public void ensureThatIsAbleToWrapExchangeAndParameterReaderAndResponseWriter(){
		final UndertowHelper helper = new UndertowHelper();
		helper.parameterReader = parameterReader;
		helper.responseWriter = responseWriter;

		final HttpServerExchange exchange = new HttpServerExchange(null, 0);
		final SimpleExchange simpleExchange = helper.simplify( exchange );

		assertEquals( simpleExchange.parameterReader, parameterReader );
		assertEquals( simpleExchange.responseWriter, responseWriter );
		assertEquals( simpleExchange.exchange, exchange );
	}
}
