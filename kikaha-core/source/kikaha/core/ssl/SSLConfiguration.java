package kikaha.core.ssl;

import kikaha.core.api.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
@Accessors( fluent = true )
public class SSLConfiguration {

	@Provided
	Configuration configuration;

	@Getter( lazy = true )
	private final String keystore = configuration.config().getString( "undertow.ssl.keystore" );

	@Getter( lazy = true )
	private final String truststore = configuration.config().getString( "undertow.ssl.truststore" );

	@Getter( lazy = true )
	private final String password = configuration.config().getString( "undertow.ssl.password" );

	@Getter( lazy = true )
	private final String securityProvider = configuration.config().getString( "undertow.ssl.security-provider" );

	public boolean isEmpty() {
		return isBlank( keystore() )
			|| isBlank( truststore() );
	}

	boolean isBlank( String str ) {
		return str == null || str.isEmpty();
	}
}
