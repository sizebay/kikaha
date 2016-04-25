package kikaha.mail;

import kikaha.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MailConfiguration {

	final String host;
	final int port;
	final String username;
	final String password;
	final String defaultSender;
	final Boolean useTLS;
	final Boolean useAuth;
	final String protocol;

	public static MailConfiguration from( Config config ) {
		return new MailConfiguration(
				config.getString( "host" ),
				config.getInteger( "port" ),
				config.getString( "username" ),
				config.getString( "password" ),
				config.getString( "default-sender" ),
				config.getBoolean( "use-tls" ),
				config.getBoolean( "use-auth" ),
				config.getString( "protocol" ) );
	}
}
