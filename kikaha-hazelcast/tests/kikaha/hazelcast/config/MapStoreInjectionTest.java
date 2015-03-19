package kikaha.hazelcast.mapstore;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import kikaha.hazelcast.Source;
import kikaha.hazelcast.config.HazelcastTestCase;
import lombok.SneakyThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.hazelcast.core.IMap;

@RunWith( MockitoJUnitRunner.class )
public class MapStoreInjectionTest extends HazelcastTestCase {

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

	@SneakyThrows
	public void provideExtraDependencies( ServiceProvider provider ) {
		provider.providerFor( Store.class, mockedStore );
	}

	public static class Data implements Serializable {
		private static final long serialVersionUID = 7392064664318236619L;
	}
}