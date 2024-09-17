package kikaha.core.modules.http.ssl;

import kikaha.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.xnio.IoUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashSet;

/**
 * Creates SSLContext for SSL environments.
 *
 * @author Miere Teixeira
 */
@Slf4j
@Singleton
public class SSLContextFactory {

	@Inject
	Config configuration;

	Config httpsConfig;

	@PostConstruct
	public void loadHttpsConfiguration(){
		httpsConfig = configuration.getConfig("server.https");
	}

	/**
	 * Create a SSLContext.
	 * 
	 * @return
	 * @throws IOException
	 */
	public SSLContext createSSLContext() throws IOException {
		if ( !httpsConfig.getBoolean("enabled") )
			return null;
		return createSSLContext(
				httpsConfig.getString("keystore"),
				httpsConfig.getString("truststore"),
				httpsConfig.getString("password") );
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
		final KeyStore keystore = loadKeyStore( keyStoreName, keystorePassword );
		final KeyStore truststore = loadKeyStore( trustStoreName, keystorePassword );
		log.debug( "Keystore and Truststore loaded. Creating SSLContext..." );
		return createSSLContext( keystore, truststore, keystorePassword );
	}

	public KeyStore loadKeyStore( final String name, final String password ) throws IOException {
		if ( name == null || name.isEmpty() )
			return null;

		final InputStream stream = openFile( name );
		if ( stream == null ){
			final String msg = "Could not open " + name + " certificate.";
			throw new IOException( msg );
		}

		log.info( "Loading key store " + name );
		return loadKeyStore( stream, password );
	}

	InputStream openFile( final String name ) {
		try {
			log.debug( "Opening certificate from the ClassPath: " + name );
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream( name );

			if ( inputStream == null ) {
				log.debug("Opening certificate from the file system: " + name);
				inputStream = new FileInputStream(name);
			}

			return inputStream;
		} catch ( FileNotFoundException e ) {
			return null;
		}
	}

	public KeyStore loadKeyStore( final InputStream stream, final String password ) throws IOException {
		try {
			final KeyStore loadedKeystore = KeyStore.getInstance( httpsConfig.getString("keystore-security-provider") );
			loadedKeystore.load( stream, password.toCharArray() );
			return loadedKeystore;
		} catch ( KeyStoreException | NoSuchAlgorithmException | CertificateException e ) {
			showAvailableSecurityProviders();
			throw new IOException("Unable to load KeyStore", e);
		} catch ( IOException e ){
			showAvailableSecurityProviders();
			throw e;
		} finally {
			IoUtils.safeClose( stream );
		}
	}

	private void showAvailableSecurityProviders(){
		HashSet<String> strings = new HashSet<>();
		for ( Provider provider : Security.getProviders() ) {
			for ( Provider.Service serviceProvider : provider.getServices() )
				strings.add( serviceProvider.getAlgorithm() );
		}

		log.debug( "Available security provides: " + String.join( " " , strings ) );
	}

	public SSLContext createSSLContext( 
		final KeyStore keyStore, final KeyStore trustStore, final String keystorePassword )
			throws IOException
	{
		final KeyManager[] keyManagers = createKeyManagers( keyStore, keystorePassword );
		final TrustManager[] trustManagers = createTrustManagers( trustStore );
		return createSSLContext( keyManagers, trustManagers );
	}

	SSLContext createSSLContext( KeyManager[] keyManagers, TrustManager[] trustManagers ) throws IOException {
		try {
			SSLContext sslContext = SSLContext.getInstance( httpsConfig.getString("cert-security-provider") );
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
			final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
			trustManagerFactory.init( trustStore );
			return trustManagerFactory.getTrustManagers();
		} catch ( NoSuchAlgorithmException | KeyStoreException e ) {
			throw new IOException( "Unable to initialise TrustManager[]", e );
		}
	}

	KeyManager[] createKeyManagers( final KeyStore keyStore, final String keystorePassword ) throws IOException {
		try {
			final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
			keyManagerFactory.init( keyStore, keystorePassword.toCharArray() );
			return keyManagerFactory.getKeyManagers();
		} catch ( NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e ) {
			throw new IOException( "Unable to initialise KeyManager[]", e );
		}
	}
}
