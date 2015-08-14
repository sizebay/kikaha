package kikaha.hazelcast.config;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProvider;

import com.hazelcast.core.Hazelcast;
import com.typesafe.config.Config;

@RunWith( MockitoJUnitRunner.class )
public abstract class HazelcastTestCase {

	@Before
	@SneakyThrows
	public void injectDependencies() {
		val config = DefaultConfiguration.loadDefaultConfiguration();
		val provider = new DefaultServiceProvider();
		provider.providerFor( Config.class, config.config() );
		provider.providerFor( Configuration.class, config );

		System.setProperty( "hazelcast.config", "tests/hazelcast-test.xml" );
		provideExtraDependencies( provider );

		provider.provideOn( this );
		afterProvideDependencies();
	}

	protected void afterProvideDependencies() {}

	protected void provideExtraDependencies( final ServiceProvider provider ) {}

	@After
	public void shutdownHazelcast() {
		Hazelcast.shutdownAll();
	}
}