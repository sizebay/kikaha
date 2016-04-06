package kikaha.core;

import kikaha.config.Config;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class ConfigurationInjectionBehaviorTest {

	@Inject
	Config config;

	@Test
	public void ensureThatCanInjectAConfiguration(){
		assertNotNull( config );
	}
}
