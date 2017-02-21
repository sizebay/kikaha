package kikaha.cloud.aws;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import com.amazonaws.ClientConfiguration;
import kikaha.core.cdi.DefaultServiceProvider;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonClientConfigurationProducerTest {

	@Mock
	AmazonClientProgrammaticConfiguration configuration;
	AmazonClientConfigurationProducer producer;

	@Before
	public void configureTest(){
		producer = new DefaultServiceProvider().load( AmazonClientConfigurationProducer.class );
		producer.listeners.add( configuration );
	}

	@Test
	public void callConfiguratorsWhenAConfigurationIsCreated() throws Exception {
		final ClientConfiguration clientConfiguration = producer.produceClientConfiguration();
		assertNotNull( clientConfiguration );
		verify( configuration ).configure( any( ClientConfiguration.class ) );
	}

	@Test
	public void callConfiguratorsOnlyOnceEvenIfTheProducerIsCalledTwiceOrMore() throws Exception {
		producer.produceClientConfiguration();
		producer.produceClientConfiguration();
		verify( configuration, times(1) ).configure( any( ClientConfiguration.class ) );
	}

	@Test
	public void theProducedConfigurationWillBeAlwaysTheSameEvenIfCalledMoreThanOnce() throws Exception {
		final ClientConfiguration first = producer.produceClientConfiguration();
		final ClientConfiguration second = producer.produceClientConfiguration();
		assertEquals( first, second );
	}
}