package kikaha.core;

import java.io.IOException;

import kikaha.core.api.Configuration;
import kikaha.core.api.UndertowStandaloneException;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class Main {

	private final Configuration configuration;
	private UndertowServer undertowServer;

	public void start() throws UndertowStandaloneException {
		undertowServer = new UndertowServer( configuration );
		undertowServer.start();
	}

	public void stop() {
		if ( undertowServer != null )
			undertowServer.stop();
	}

	public static void main( String[] args ) throws InterruptedException, UndertowStandaloneException, IOException, ClassNotFoundException {
		final Configuration config = args.length == 0 || isBlank( args[0] )
				? DefaultConfiguration.loadDefaultConfiguration()
				: DefaultConfiguration.loadConfiguration( args[0] );
		val main = new Main( config );
		main.start();
	}

	static boolean isBlank( String string ) {
		return string == null || string.isEmpty();
	}
}