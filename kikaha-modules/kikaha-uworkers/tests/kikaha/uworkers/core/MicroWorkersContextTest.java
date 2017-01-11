package kikaha.uworkers.core;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import javax.inject.*;
import kikaha.config.Config;
import kikaha.core.test.KikahaRunner;
import kikaha.uworkers.local.LocalEndpointFactory;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class MicroWorkersContextTest {

	@Inject
    MicroWorkersContext context;
	@Inject Config config;

	@Before
	public void overrideContextWithOriginalConfig(){
		context.config = config;
	}

	@Test
	public void ensureThatIsAbleToRetrieveTheConfiguredParallelismRate(){
		context.config = spy(context.config);
		final int endpointParallelism = context.getEndpointParallelism("configured", 0);
		verify( context.config ).getInteger( eq( "server.uworkers.configured.parallelism" ), eq(0) );
		assertEquals( 2, endpointParallelism );
	}

	@Test
	public void ensureThatIsAbleToRetrieveAEndpointFactoryForAGivenEndpointName(){
		final EndpointFactory factory = context.getFactoryFor("any-value-name");
		assertNotNull(factory);
		assertTrue(LocalEndpointFactory.class.isInstance( factory ));
	}
}
