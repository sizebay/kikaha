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

	final String keystore;
	final String truststore;
	final String password;
	final String certSecurityProvider;
	final String keystoreSecurityProvider;

	public DefaultSSLConfiguration( final Config config ) {
		this.keystore = config.getString( "keystore" );
		this.truststore = config.getString( "truststore" );
		this.password = config.getString( "password" );
		this.certSecurityProvider = config.getString( "cert-security-provider" );
		this.keystoreSecurityProvider = config.getString( "keystore-security-provider" );
	}

	@Override
	public boolean isEmpty() {
		return isBlank( keystore() )
			&& isBlank( truststore() );
	}

	boolean isBlank( String str ) {
		return str == null || str.isEmpty();
	}
}
