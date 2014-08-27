package kikaha.core.ssl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import kikaha.core.DefaultConfiguration;
import kikaha.core.api.Configuration;
import lombok.val;

import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SSLContextFactoryTest {

	@Test
	public void ensureCannotToCreateASSLContextFromEmptyConfiguration() throws IOException {
		val config = createEmptyConfiguration();
		val factory = createFactoryFrom( config );
		val context = factory.createSSLContext();
		assertNull( context );
	}

	Configuration createEmptyConfiguration() {
		final Config defaultConfiguration = ConfigFactory.load();
		final Config reference = ConfigFactory.load( "META-INF/reference" ).withFallback( defaultConfiguration );
		return new DefaultConfiguration( reference, "default" );
	}

	@Test
	public void ensureIsPossibleToCreateASSLContextFromDefaultConfiguration() throws IOException {
		val config = DefaultConfiguration.loadDefaultConfiguration();
		val factory = createFactoryFrom( config );
		val context = factory.createSSLContext();
		assertNotNull( context );
	}

	SSLContextFactory createFactoryFrom( final Configuration config ) {
		val sslConfig = new SSLConfiguration();
		sslConfig.configuration = config;
		val factory = new SSLContextFactory();
		factory.sslConfiguration = sslConfig;
		return factory;
	}

}
