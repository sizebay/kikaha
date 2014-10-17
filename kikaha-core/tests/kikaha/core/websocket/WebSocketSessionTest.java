package kikaha.core.websocket;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class WebSocketSessionTest {

	@Mock
	WebSocketHttpExchange exchange;

	@Mock
	WebSocketChannel channel;

	@Mock
	Map<String, List<String>> requestHeaders;

	@Mock
	Map<String, List<String>> requestParameters;

	@Mock
	Map<String, List<String>> responseHeadears;

	@Mock
	Principal userPrincipal;

	@Before
	public void populateExchange() {
		doReturn( requestHeaders ).when( exchange ).getRequestHeaders();
		doReturn( requestParameters ).when( exchange ).getRequestParameters();
		doReturn( responseHeadears ).when( exchange ).getResponseHeaders();
		doReturn( userPrincipal ).when( exchange ).getUserPrincipal();
	}

	@Test
	public void ensureThatRequiredFieldsFromExchangeWasDelegatedToSession() {
		final WebSocketSession session = createSession();
		assertThat( session.originalExchange(), is( exchange ) );
		assertThat( session.channel(), is( channel ) );
		assertThat( session.requestHeaders(), is( requestHeaders ) );
		assertThat( session.requestParameters(), is( requestParameters ) );
		assertThat( session.responseHeaders(), is( responseHeadears ) );
		assertThat( session.userPrincipal(), is( userPrincipal ) );
	}

	@Test
	public void ensureThatExchangeAndChannelAndResponseHeadersWereCleanedAsExpected() {
		final WebSocketSession session = createSession();
		session.clean();
		assertThat( session.requestHeaders(), is( requestHeaders ) );
		assertThat( session.requestParameters(), is( requestParameters ) );
		assertThat( session.userPrincipal(), is( userPrincipal ) );
		assertThat( session.responseHeaders(), nullValue() );
		assertThat( session.originalExchange(), nullValue() );
		assertThat( session.channel(), nullValue() );
	}

	WebSocketSession createSession() {
		final WebSocketSession session = new WebSocketSession( exchange );
		session.channel( channel );
		return session;
	}
}
