package kikaha.core;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Main {

	private final Configuration configuration;
	private UndertowServer undertowServer;

	public void start() {
		try {
			undertowServer = new UndertowServer( configuration );
			undertowServer.start();
		// UNCHECKED: It should handle any exception thrown here
		} catch ( Throwable cause ) {
		// CHECKED
			log.error("Can't start Kikaha", cause);
			System.exit(1);
		}
	}

	public void stop() {
		if ( undertowServer != null )
			undertowServer.stop();
	}

	public static void main( final String[] args ) throws Exception {
		val config = DefaultConfiguration.loadDefaultConfiguration();
		if ( args.length > 0 && !isBlank( args[0] ) )
			config.resourcesPath( args[0] );
		val main = new Main( config );
		main.start();
	}

	static boolean isBlank( final String string ) {
		return string == null || string.isEmpty();
	}
}