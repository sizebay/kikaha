package kikaha.core.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import kikaha.core.api.conf.Configuration;
import kikaha.core.api.conf.SSLConfiguration;
import lombok.val;

import org.xnio.IoUtils;

import trip.spi.Provided;
import trip.spi.Singleton;

/**
 * Creates SSLContext for SSL environments.
 *
 * @author Miere Teixeira
 */
@Singleton
// TODO: improve capability of SSL support making more integration tests.
public class SSLContextFactory {

	@Provided
	Configuration configuration;

	/**
	 * Create a SSLContext based on data defined in {@link SSLConfiguration}.
	 * 
	 * @return
	 * @throws IOException
	 */
	public SSLContext createSSLContext() throws IOException {
		if ( configuration.ssl().isEmpty() )
			return null;
		return createSSLContext(
			configuration.ssl().keystore(),
			configuration.ssl().truststore(),
			configuration.ssl().password() );
	}

	/**
	 * Create a SSLContext for a given {@code truststore} and {@code keystore}
	 * files. Note that it is expected that both keys have the same password.
	 * 
	 * @param keyStoreName
	 * @param trustStoreName
	 * @param keystorePassword
	 * @return
	 * @throws IOException
	 */
	public SSLContext createSSLContext(
		final String keyStoreName, final String trustStoreName, final String keystorePassword )
			throws IOException {
		val keystore = loadKeyStore( keyStoreName, keystorePassword );
		val truststore = loadKeyStore( trustStoreName, keystorePassword );
		return createSSLContext( keystore, truststore, keystorePassword );
	}

	public KeyStore loadKeyStore( final String name, final String password ) throws IOException {
		if ( name == null || name.isEmpty() )
			return null;

		val stream = openFile( name );
		if ( stream == null ){
			val msg = "Could not open " + name + " certificate.";
			throw new IOException( msg );
		}

		System.out.println( "INFO: loading key store " + name );
		return loadKeyStore( stream, password );
	}

	InputStream openFile( final String name ) {
		try {
			return new FileInputStream( name );
		} catch ( FileNotFoundException e ) {
			return getClass().getClassLoader().getResourceAsStream( name );
		}
	}

	public KeyStore loadKeyStore( final InputStream stream, final String password ) throws IOException {
		try {
			val loadedKeystore = KeyStore.getInstance( configuration.ssl().keystoreSecurityProvider() );
			loadedKeystore.load( stream, password.toCharArray() );
			return loadedKeystore;
		} catch ( KeyStoreException | NoSuchAlgorithmException | CertificateException e ) {
			throw new IOException( "Unable to load KeyStore", e );
		} finally {
			IoUtils.safeClose( stream );
		}
	}

	public SSLContext createSSLContext( 
		final KeyStore keyStore, final KeyStore trustStore, final String keystorePassword )
			throws IOException
	{
		val keyManagers = createKeyManagers( keyStore, keystorePassword );
		val trustManagers = createTrustManagers( trustStore );
		return createSSLContext( keyManagers, trustManagers );
	}

	SSLContext createSSLContext( KeyManager[] keyManagers, TrustManager[] trustManagers ) throws IOException {
		try {
			val sslContext = SSLContext.getInstance( configuration.ssl().certSecurityProvider() );
			sslContext.init( keyManagers, trustManagers, null );
			return sslContext;
		} catch ( NoSuchAlgorithmException | KeyManagementException e ) {
			throw new IOException( "Unable to create and initialise the SSLContext", e );
		}
	}

	TrustManager[] createTrustManagers( final KeyStore trustStore ) throws IOException {
		if ( trustStore == null )
			return null;
		try {
			val trustManagerFactory = TrustManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
				trustManagerFactory.init( trustStore );
			return trustManagerFactory.getTrustManagers();
		} catch ( NoSuchAlgorithmException | KeyStoreException e ) {
			throw new IOException( "Unable to initialise TrustManager[]", e );
		}
	}

	KeyManager[] createKeyManagers( final KeyStore keyStore, final String keystorePassword ) throws IOException {
		try {
			val keyManagerFactory = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
			keyManagerFactory.init( keyStore, keystorePassword.toCharArray() );
			return keyManagerFactory.getKeyManagers();
		} catch ( NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e ) {
			throw new IOException( "Unable to initialise KeyManager[]", e );
		}
	}
}
