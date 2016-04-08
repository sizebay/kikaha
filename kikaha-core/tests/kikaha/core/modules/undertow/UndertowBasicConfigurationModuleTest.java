package kikaha.core.modules.undertow;

import io.undertow.Undertow;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.OptionMap;
import tests.Exposed;

import javax.inject.Inject;
import java.io.IOException;

import static io.undertow.UndertowOptions.ENABLE_CONNECTOR_STATISTICS;
import static io.undertow.UndertowOptions.RECORD_REQUEST_START_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.xnio.Options.BACKLOG;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class UndertowBasicConfigurationModuleTest {

	@Inject
	UndertowBasicConfigurationModule undertowServerConf;

	@Test
	public void ensureThatHasSetTheDefaultValues() throws IOException {
		final Undertow.Builder builder = Undertow.builder()
				.setServerOption(ENABLE_CONNECTOR_STATISTICS, true)
				.setServerOption(RECORD_REQUEST_START_TIME, true);
		undertowServerConf.load( builder, null );
		final Exposed undertow = new Exposed( builder.build() );
		ensureUndertowHasDefaultIntValues( undertow );
		ensureUndertowHasDefaultServerOptions(undertow);
		ensureUndertowHasDefaultSocketOptions(undertow);
	}

	private void ensureUndertowHasDefaultIntValues(Exposed undertow) {
		final int bufferSize = undertow.getFieldValue("bufferSize", Integer.TYPE);
		assertEquals(16384, bufferSize);
		final int ioThreads = undertow.getFieldValue("ioThreads", Integer.TYPE);
		assertEquals( Runtime.getRuntime().availableProcessors(), ioThreads);
		final int workerThreads = undertow.getFieldValue("workerThreads", Integer.TYPE);
		assertEquals(32, workerThreads);
	}

	private void ensureUndertowHasDefaultServerOptions( Exposed undertow ){
		OptionMap serverOptions = undertow.getFieldValue("serverOptions", OptionMap.class);
		boolean connectorStatistics = serverOptions.get( ENABLE_CONNECTOR_STATISTICS );
		assertFalse(connectorStatistics);
		boolean recordStartTime = serverOptions.get( RECORD_REQUEST_START_TIME );
		assertFalse(recordStartTime);
	}

	private void ensureUndertowHasDefaultSocketOptions(Exposed undertow ) {
		OptionMap socketOptions = undertow.getFieldValue("socketOptions", OptionMap.class);
		final int backlog = socketOptions.get(BACKLOG);
		assertEquals(10000, backlog);
	}
}
