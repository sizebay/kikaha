package kikaha.core;

import java.io.IOException;

import kikaha.core.api.KikahaException;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class Main {

	private final Configuration configuration;
	private UndertowServer undertowServer;

	public void start() throws KikahaException {
		undertowServer = new UndertowServer( configuration );
		undertowServer.start();
	}

	public void stop() {
		if ( undertowServer != null )
			undertowServer.stop();
	}

	public static void main( final String[] args ) throws InterruptedException, KikahaException, IOException, ClassNotFoundException {
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