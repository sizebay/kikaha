package kikaha.core.modules.security;

import static org.junit.Assert.*;

import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for SecurityConfiguration.
 */
@RunWith( KikahaRunner.class )
public class SecurityConfigurationTest {

	@Inject SecurityConfiguration configuration;

	@Test
	public void ensureDefaultPasswordEncoderIsPlainText(){
		final PasswordEncoder passwordEncoder = configuration.getPasswordEncoder();
		assertTrue( PlainTextPasswordEncoder.class.isInstance( passwordEncoder ) );
	}
}