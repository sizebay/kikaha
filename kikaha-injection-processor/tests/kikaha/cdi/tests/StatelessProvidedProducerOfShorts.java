package kikaha.cdi.tests;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;

import kikaha.core.cdi.Stateless;

@Stateless
@Typed( ProducerOfShorts.class )
public class StatelessProvidedProducerOfShorts implements ProducerOfShorts {

	volatile short counter;

	@Override
	@Produces
	public Short produceShort() {
		return incrementAndReturn();
	}

	private short incrementAndReturn() {
		return counter++;
	}
}
