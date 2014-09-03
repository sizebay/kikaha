package kikaha.core.ssl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.val;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class SSLUndertowServer {

	static final String SCHEME = "scheme";

	Undertow undertow;

	@Before
	public void startServer() throws IOException, ServiceProviderException {
		val config = DefaultConfiguration.loadDefaultConfiguration();
		val provider = new ServiceProvider();
		provider.providerFor( Configuration.class, config );
		val sslContextFactory = provider.load( SSLContextFactory.class );
		val context = sslContextFactory.createSSLContext( "server.keystore", "server.truststore", "password" );
		startServer( context );
	}

	void startServer( final SSLContext context ) {
		undertow = Undertow
			.builder()
			.addHttpsListener( 9990, "localhost", context )
			.setHandler( new HttpHandler() {
				@Override
				public void handleRequest( final HttpServerExchange exchange ) throws Exception {
					exchange.getResponseHeaders().put( HttpString.tryFromString( SCHEME ), exchange.getRequestScheme() );
					exchange.endExchange();
				}
			} )
			.build();
		undertow.start();
	}

	@Test( timeout = 6000 )
	public void ensureThatCouldRequestAsHttps()
		throws InterruptedException, MalformedURLException, IOException,
			KeyManagementException, NoSuchAlgorithmException {
		SSLFixSSLHandshakeException.applyFixPatch();
		HttpURLConnection httpConnection = ( (HttpURLConnection)new URL( "https://localhost:9990/" ).openConnection() );
		assertThat( httpConnection.getResponseMessage(), is( "OK" ) );
		String headerValue = httpConnection.getHeaderFields().get( SCHEME ).get( 0 );
		assertThat( headerValue, is( "https" ) );
	}

	@After
	public void stopServer() {
		undertow.stop();
	}
}
