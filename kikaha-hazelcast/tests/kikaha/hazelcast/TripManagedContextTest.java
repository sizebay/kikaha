package kikaha.hazelcast;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import kikaha.Store;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.hazelcast.core.IMap;

@RunWith( MockitoJUnitRunner.class )
public class TripManagedContextTest {

	final ServiceProvider provider = new ServiceProvider();

	@Mock
	Store mockedStore;

	@Provided
	@Source( "map-configured-to-use-map-store" )
	IMap<String, Object> map;

	@Test
	public void ensureThatMapStoreHaveInjectedMock() {
		assertNotNull( map );
		map.put( "1", new Data() );
		verify( mockedStore ).loadAllKeys();
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		System.setProperty( "hazelcast.config", "tests/hazelcast-test.xml" );
		System.setProperty( "hazelcast.logging.type", "jdk" );
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		provider.providerFor( Store.class, mockedStore );
		provider.provideOn( this );
	}

	class Data implements Serializable {
		private static final long serialVersionUID = 7392064664318236619L;
	}
}