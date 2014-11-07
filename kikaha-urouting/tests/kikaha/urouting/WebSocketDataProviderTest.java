package kikaha.urouting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import java.util.Date;
import java.util.HashMap;

import kikaha.core.websocket.WebSocketSession;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

@RunWith( MockitoJUnitRunner.class )
public class WebSocketDataProviderTest {

	@Provided
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
		final HashMap<String, String> params = new HashMap<>();
		params.put( "Expires", "1234560321" );
		doReturn( params ).when( session ).requestParameters();
		final Date pathParam = provider.getPathParam( session, "Expires", Date.class );
		assertNotNull( pathParam );
		assertEquals( pathParam, new Date( 1234560321l ) );
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		new ServiceProvider().provideOn( this );
	}
}
