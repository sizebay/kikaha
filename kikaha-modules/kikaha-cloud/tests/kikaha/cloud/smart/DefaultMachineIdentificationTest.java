package kikaha.cloud.smart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import kikaha.config.Config;
import kikaha.core.modules.security.SessionIdGenerator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link DefaultMachineIdentification}.
 */
@RunWith( MockitoJUnitRunner.class )
public class DefaultMachineIdentificationTest {

	@Mock Config config;

	@InjectMocks
	@Spy
    DefaultMachineIdentification identification;

	@Test
	@Ignore
	public void ensureCanResolveTheIpAddressAsExpected(){
		doReturn( "wlp9s0" ).when( config ).getString( eq("server.smart-server.local-address.default-interface" ), anyString() );
		doReturn( true ).when( config ).getBoolean( eq("server.smart-server.local-address.ipv4-only"), eq(true) );
		final String localAddress = identification.getLocalAddress();
		assertEquals( "192.168.0.137", localAddress );
	}

	@Test
	public void ensureTheDefaultGeneratedMachineIdIsTheMacAddress() throws Exception {
		final String machineId = identification.generateTheMachineId();
		assertEquals( SessionIdGenerator.MAC_ADDRESS, machineId );
	}
}