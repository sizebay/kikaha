package io.skullabs.undertow.hazelcast;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import kikaha.core.DefaultConfiguration;

import com.typesafe.config.Config;

import org.junit.Test;

public class TypesafeConfigTest {

	final Config config = DefaultConfiguration.loadDefaultConfig();

	@Test
	public void grant() {
		TypesafeConfigurationCompatibilization compatibilization = spy( new TypesafeConfigurationCompatibilization( config ) );
		compatibilization.compatibilize();
		verify( compatibilization, times( 1 ) ).memorizeProperty( any( String.class ), any( Object.class ) );
	}
}
