package kikaha.core.modules.security;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for PlainTextPasswordEncoder.
 */
public class PlainTextPasswordEncoderTest {

	final PasswordEncoder encoder = new PlainTextPasswordEncoder();

	@Test
	public void ensureEncodedPasswordIsSameAsOriginalPassword() throws Exception {
		final String encoded = encoder.encode( "123" );
		assertEquals( "123", encoded );
	}

	@Test
	public void ensureCanMatchAPasswordEncodedAsPlainText() throws Exception {
		final String encoded = encoder.encode( "123" );
		assertTrue( encoder.matches( "123", encoded ) );
	}
}