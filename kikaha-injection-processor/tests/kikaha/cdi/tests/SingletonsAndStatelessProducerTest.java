package kikaha.cdi.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import kikaha.core.cdi.DefaultCDI;
import kikaha.core.cdi.ServiceProviderException;

import org.junit.Test;

public class SingletonsAndStatelessProducerTest {

	final DefaultCDI provider = new DefaultCDI();

	@Test
	public void ensureThatProduceThreeDifferentNumbers() throws ServiceProviderException {
		assertThat( provider.load( Integer.class ), is( 1 ) );
		assertThat( provider.load( Integer.class ), is( 2 ) );
		assertThat( provider.load( Integer.class ), is( 3 ) );
	}

	@Test
	public void ensureThatCantProduceThreeDifferentShorts() throws ServiceProviderException {
		assertThat( provider.load( Short.class ), is( (short)0 ) );
		assertThat( provider.load( Short.class ), is( (short)0 ) );
		assertThat( provider.load( Short.class ), is( (short)0 ) );
	}

	@Test
	public void ensureThatCanProduceUnrepeatedShortsWhenManuallyCreated(){
		final ProducerOfShorts producerOfShorts = new StatelessProvidedProducerOfShorts();
		assertThat( producerOfShorts.produceShort(), is( (short)0 ) );
		assertThat( producerOfShorts.produceShort(), is( (short)1 ) );
		assertThat( producerOfShorts.produceShort(), is( (short)2 ) );
	}

	@Test
	public void ensureThatCantProduceUnrepeatedShortsWhenCreatedByServiceProvider() throws ServiceProviderException{
		ProducerOfShorts producerOfShorts = provider.load( ProducerOfShorts.class );
		assertThat( producerOfShorts.produceShort(), is( (short)0 ) );
		producerOfShorts = provider.load( ProducerOfShorts.class );
		assertThat( producerOfShorts.produceShort(), is( (short)0 ) );
		producerOfShorts = provider.load( ProducerOfShorts.class );
		assertThat( producerOfShorts.produceShort(), is( (short)0 ) );
	}

	@Test
	public void ensureThatCanListStatelessClassForProducerOfShort() {
		final Iterable<Class<ProducerOfShorts>> implementations = provider.loadClassesImplementing( ProducerOfShorts.class );
		assertNotNull( implementations );
		for ( final Class<ProducerOfShorts> cls : implementations )
			assertNotNull( cls );
	}
}
