package kikaha.core.impl.conf;

import kikaha.core.api.conf.SSLConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;

@Getter
@RequiredArgsConstructor
@Accessors( fluent = true )
public class DefaultSSLConfiguration implements SSLConfiguration {

	final Config config;

	@Getter( lazy = true )
	private final String keystore = config().getString( "keystore" );

	@Getter( lazy = true )
	private final String truststore = config().getString( "truststore" );

	@Getter( lazy = true )
	private final String password = config().getString( "password" );

	@Getter( lazy = true )
	private final String certSecurityProvider = config().getString( "cert-security-provider" );

	@Getter( lazy = true )
	private final String keystoreSecurityProvider = config().getString( "keystore-security-provider" );

	@Override
	public boolean isEmpty() {
		return isBlank( keystore() )
			&& isBlank( truststore() );
	}

	boolean isBlank( String str ) {
		return str == null || str.isEmpty();
	}
}
