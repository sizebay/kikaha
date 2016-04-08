package kikaha.core.modules.http.ssl;

import kikaha.config.Config;
import kikaha.config.MergeableConfig;
import kikaha.core.test.KikahaRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(KikahaRunner.class)
public class SSLContextFactoryTest {

	@Inject
	SSLContextFactory factory;

	@Test
	public void ensureCannotToCreateASSLContextFromEmptyConfiguration() throws IOException {
		Config httpsConfig = mock(Config.class);
		doReturn( false ).when( httpsConfig ).getBoolean( "enabled" );
		factory.httpsConfig = httpsConfig;

		final SSLContext sslContext = factory.createSSLContext();
		assertNull( sslContext );
	}

	@Test
	@Ignore
	public void ensureIsPossibleToCreateASSLContextFromDefaultConfiguration() throws IOException {
		final Config config = MergeableConfig.create().load(new File("tests-resources/ssl-configuration.yml"));
		final Config httpsConfig = config.getConfig("server.https");
		factory.httpsConfig = httpsConfig;

		final SSLContext sslContext = factory.createSSLContext();
		assertNotNull( sslContext );
	}
}
