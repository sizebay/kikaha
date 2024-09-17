package kikaha.bcrypt;

import static org.junit.Assert.assertTrue;
import javax.inject.Inject;
import kikaha.core.modules.security.SecurityConfiguration;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class BCryptPasswordEncoderTest {

	static final String
		PREVIOUSLY_ENCODED_PASS = "$2a$12$FFv9BcH1mXKAg.9jb2MR4.nq9aoVy5jJk5AAb/f3C8jArMfYCWapu",
		RAW_PASS = "123456";

	@Inject SecurityConfiguration securityConfiguration;

	@Test
	public void encode() throws Exception {
		final String encoded = securityConfiguration.getPasswordEncoder().encode(RAW_PASS);
		assertTrue( securityConfiguration.getPasswordEncoder().matches( RAW_PASS, encoded ) );
	}

	@Test
	public void matches() throws Exception {
		assertTrue( securityConfiguration.getPasswordEncoder().matches( RAW_PASS, PREVIOUSLY_ENCODED_PASS) );
	}

}