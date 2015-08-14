package kikaha.hazelcast.config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import kikaha.core.impl.conf.DefaultConfiguration;

import org.junit.Test;

import com.typesafe.config.Config;

public class TypesafeConfigTest {

	final Config config = DefaultConfiguration.loadDefaultConfig();

	@Test
	public void grant() {
		TypesafeConfigurationCompatibilization compatibilization = spy( new TypesafeConfigurationCompatibilization( config ) );
		compatibilization.compatibilize();
		verify( compatibilization, times( 2 ) ).memorizeProperty( any( String.class ), any( Object.class ) );
	}
}
