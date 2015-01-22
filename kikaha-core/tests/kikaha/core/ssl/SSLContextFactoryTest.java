package kikaha.core.ssl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.val;

import org.junit.Ignore;
import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Ignore
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
		val factory = new SSLContextFactory();
		factory.configuration = config;
		return factory;
	}
}
