package kikaha.cloud.smart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import kikaha.config.Config;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link DefaultLocalAddressResolver}.
 */
@Ignore
@RunWith( MockitoJUnitRunner.class )
public class DefaultLocalAddressResolverTest {

	@Mock Config config;

	@InjectMocks
	@Spy DefaultLocalAddressResolver resolver;

	@Test
	public void ensureCanResolveTheIpAddressAsExpected(){
		doReturn( "wlp9s0" ).when( config ).getString( eq("server.smart-server.local-address.default-interface" ), anyString() );
		doReturn( true ).when( config ).getBoolean( eq("server.smart-server.local-address.ipv4-only"), eq(true) );
		final String localAddress = resolver.getLocalAddress();
		assertEquals( "192.168.0.137", localAddress );
	}
}