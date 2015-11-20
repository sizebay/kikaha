package kikaha.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.typesafe.config.Config;

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
				config.getInt( "port" ),
				config.getString( "username" ),
				config.getString( "password" ),
				config.getString( "default-sender" ),
				config.getBoolean( "use-tls" ),
				config.getBoolean( "use-auth" ),
				config.getString( "protocol" ) );
	}
}
