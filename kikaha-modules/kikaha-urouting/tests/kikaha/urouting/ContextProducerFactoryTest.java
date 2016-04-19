package kikaha.urouting;

import kikaha.core.test.KikahaRunner;
import kikaha.urouting.api.ContextProducer;
import kikaha.urouting.api.ContextProducerFactory;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.samples.StringProducer;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith( KikahaRunner.class )
public class ContextProducerFactoryTest {

	@Inject
	ContextProducerFactory producerFactory;

	@Test
	public void grantThatProducesAHelloWorld() throws RoutingException {
		final ContextProducer<String> stringProducer = producerFactory.producerFor( String.class );
		assertThat( stringProducer.produce( null ), is( StringProducer.HELLO_WORLD ) );
	}
}
