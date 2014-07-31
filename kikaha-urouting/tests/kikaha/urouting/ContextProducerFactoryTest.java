package kikaha.urouting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.ContextProducerFactory;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.samples.StringProducer;

import org.junit.Test;

import trip.spi.Provided;

public class ContextProducerFactoryTest extends TestCase {

	@Provided
	ContextProducerFactory producerFactory;

	@Test
	public void grantThatProducesAHelloWorld() throws RoutingException {
		final ContextProducer<String> stringProducer = producerFactory.producerFor( String.class );
		assertThat( stringProducer.produce( null ), is( StringProducer.HELLO_WORLD ) );
	}
}
