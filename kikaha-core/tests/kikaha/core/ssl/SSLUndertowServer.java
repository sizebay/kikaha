package kikaha.core.ssl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.SSLContext;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProviderException;

public class SSLUndertowServer {

	static final String SCHEME = "scheme";

	Undertow undertow;

	@Before
	public void startServer() throws IOException, ServiceProviderException {
		val config = DefaultConfiguration.loadDefaultConfiguration();
		val provider = new DefaultServiceProvider();
		provider.providerFor( Configuration.class, config );
		val sslContextFactory = provider.load( SSLContextFactory.class );
		val context = sslContextFactory.createSSLContext( "tests/server.keystore", "tests/server.truststore", "password" );
		startServer( context );
	}

	void startServer( final SSLContext context ) {
		undertow = Undertow
			.builder()
			.addHttpsListener( 9990, "localhost", context )
			.setHandler( this::handleSSLRequest )
			.build();
		undertow.start();
	}

	void handleSSLRequest( final HttpServerExchange exchange ) throws Exception {
		exchange.getResponseHeaders().put( HttpString.tryFromString( SCHEME ), exchange.getRequestScheme() );
		exchange.endExchange();
	}

	@Test( timeout = 6000 )
	@SneakyThrows
	public void ensureThatCouldRequestAsHttps() {
		SSLFixSSLHandshakeException.applyFixPatch();
		final HttpURLConnection httpConnection = (HttpURLConnection)new URL( "https://localhost:9990/" ).openConnection();
		assertThat( httpConnection.getResponseMessage(), is( "OK" ) );
		final String headerValue = httpConnection.getHeaderFields().get( SCHEME ).get( 0 );
		assertThat( headerValue, is( "https" ) );
	}

	@After
	public void stopServer() {
		undertow.stop();
	}
}
