package kikaha.cdi.tests;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import kikaha.core.cdi.DefaultServiceProvider;
import kikaha.core.cdi.ServiceProviderException;

import org.junit.Test;

public class InjectionOfServiceDefinedByItsExposedTypeTest {

	final DefaultServiceProvider provider = new DefaultServiceProvider();

	@Inject
	@Typed( Bean.class )
	SerializableBean bean;

	@Test
	public void ensureThatAreAbleToLoadSerializableBeanExposedAsSerializable() throws ServiceProviderException {
		final Bean loaded = provider.load( Bean.class );
		assertThat( loaded, instanceOf( SerializableBean.class ) );
	}

	@Test
	public void ensureThatCouldProvideSerializableBeanExposedAsSerializable() throws ServiceProviderException {
		provider.provideOn( this );
		assertNotNull( bean );
		assertThat( bean, instanceOf( SerializableBean.class ) );
	}
}
