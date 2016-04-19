package kikaha.urouting;

import kikaha.core.modules.websocket.WebSocketSession;
import kikaha.core.test.KikahaRunner;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

@RunWith( KikahaRunner.class )
public class WebSocketDataProviderTest {

	@Inject
	WebSocketDataProvider provider;

	@Mock
	WebSocketSession session;

	@Test
	@SneakyThrows
	public void ensureThatCouldProvideAPathParameter() {
		final HashMap<String, String> params = new HashMap<>();
		params.put( "price", "123456.0321" );
		doReturn( params ).when( session ).requestParameters();
		final Double pathParam = provider.getPathParam( session, "price", Double.class );
		assertNotNull( pathParam );
		assertEquals( pathParam, 123456.0321d, 0 );
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldProvideAHeaderParameter() {
		final HashMap<String, List<String>> params = new HashMap<>();
		params.put( "Expires", Arrays.asList( "1234560321" ) );
		doReturn( params ).when( session ).requestHeaders();
		final Date pathParam = provider.getHeaderParam( session, "Expires", Date.class );
		assertNotNull( pathParam );
		assertEquals( pathParam, new Date( 1234560321l ) );
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		MockitoAnnotations.initMocks(this);
	}
}
