package kikaha.bcrypt;

import kikaha.core.modules.security.PasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 */
public class BCryptPasswordEncoder implements PasswordEncoder {

	static final int LOG_ROUNDS = 12;

	@Override
	public String encode(String rawPassword) {
		return BCrypt.hashpw( rawPassword, BCrypt.gensalt(LOG_ROUNDS) );
	}

	@Override
	public boolean matches(String rawPassword, String encodedPassword) {
		return BCrypt.checkpw(rawPassword, encodedPassword);
	}
}
