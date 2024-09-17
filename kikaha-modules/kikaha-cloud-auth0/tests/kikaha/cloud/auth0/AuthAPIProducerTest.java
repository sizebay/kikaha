package kikaha.cloud.auth0;

import static org.junit.Assert.*;
import javax.inject.Inject;
import kikaha.core.test.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class AuthAPIProducerTest {

	@Inject Auth0.AuthConfig injectedAuthAPI;

	@Test
	public void ensureThatCanInjectAuthAPI(){
		assertNotNull( injectedAuthAPI );
	}

	@Test
	public void ensureThatIsAbleToCreateAValidAuthAPIFromConfigurationFile(){
		//assertEquals( "https://localhost/",  );
		assertEquals( "unknown.auth0.com", injectedAuthAPI.clientDomain );
		assertEquals( "bad-client-id", injectedAuthAPI.clientId );
		assertEquals( "bad-client-secret", injectedAuthAPI.clientSecret );
		assertEquals( "/not/configured/path", injectedAuthAPI.publicKeyPath );
		assertEquals( "HS512", injectedAuthAPI.signingAlgorithm );
		assertFalse( injectedAuthAPI.base64EncodedSecret );
	}
}