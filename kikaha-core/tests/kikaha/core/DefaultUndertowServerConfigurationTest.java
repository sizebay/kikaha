package kikaha.core;

import static io.undertow.UndertowOptions.ENABLE_CONNECTOR_STATISTICS;
import static io.undertow.UndertowOptions.RECORD_REQUEST_START_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;

import java.lang.reflect.Field;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.DefaultUndertowServerConfiguration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.xnio.OptionMap;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.typesafe.config.Config;

public class DefaultUndertowServerConfigurationTest {

	@Provided
	DefaultUndertowServerConfiguration undertowServerConf;
	
	@Test
	public void ensureThatHasSetTheDefaultValues(){
		Builder builder = Undertow.builder()
				.setServerOption(ENABLE_CONNECTOR_STATISTICS, true)
				.setServerOption(RECORD_REQUEST_START_TIME, true);
		undertowServerConf.configure(builder);
		Exposed undertow = new Exposed( builder.build() );
		ensureUndertowHasDefaultIntValues( undertow );
		ensureUndertowHasDefaultServerOptions(undertow);
	}

	private void ensureUndertowHasDefaultIntValues(Exposed undertow) {
		int bufferSize = undertow.getFieldValue("bufferSize", Integer.TYPE);
		assertEquals(16384, bufferSize);
		int ioThreads = undertow.getFieldValue("ioThreads", Integer.TYPE);
		assertEquals(2, ioThreads);
		int workerThreads = undertow.getFieldValue("workerThreads", Integer.TYPE);
		assertEquals(200, workerThreads);
	}

	private void ensureUndertowHasDefaultServerOptions( Exposed undertow ){
		val serverOptions = undertow.getFieldValue("serverOptions", OptionMap.class);
		val connectorStatistics = serverOptions.get( ENABLE_CONNECTOR_STATISTICS );
		assertFalse(connectorStatistics);
		val recordStartTime = serverOptions.get( RECORD_REQUEST_START_TIME );
		assertFalse(recordStartTime);
	}

	@Before
	@SneakyThrows
	public void setup() {
		val serviceProvider = new ServiceProvider();
		val configuration = DefaultConfiguration.loadDefaultConfiguration();
		serviceProvider.providerFor( Configuration.class, configuration );
		serviceProvider.providerFor( Config.class, configuration.config() );
		serviceProvider.provideOn( this );
	}
	
	@RequiredArgsConstructor
	class Exposed {

		final Object undertow;

		@SneakyThrows
		@SuppressWarnings("unchecked")
		public <T> T getFieldValue( String name, Class<T> target ) {
			final Field field = undertow.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return (T)field.get( undertow );
		}
	}
}
