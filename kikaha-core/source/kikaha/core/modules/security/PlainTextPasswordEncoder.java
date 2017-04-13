package kikaha.core.modules.security;

/**
 *
 */
public class PlainTextPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode( String rawPassword ) {
		return rawPassword;
	}

	@Override
	public boolean matches( String rawPassword, String encodedPassword ) {
		return rawPassword.equals( encodedPassword );
	}
}
