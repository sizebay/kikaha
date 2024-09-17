package kikaha.core.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class ProviderTest {

	final DefaultCDI provider = new DefaultCDI();
	
	@Before
	public void grantThatProvidedHasNoCachedData() {
		Iterable<?> nullIterable = provider.dependencies.get( Printable.class );
		assertNull(nullIterable);
		nullIterable = provider.injectionContext.implementedClasses.get( Printable.class );
		assertNull(nullIterable);
	}

	@Test
	public void grantThatInjectTestableResourcesButKeepItCachedAsExpected() throws ServiceProviderException {
		grantThatRetrieveAllClassesThatImplementsAnInterface();

		final Iterable<Class<?>> implementations = provider.injectionContext.implementedClasses.get( Printable.class );
		grantThatRetrieveAWellImplementedPrintableInstanceAsExpected();
		assertEquals( implementations, provider.injectionContext.implementedClasses.get( Printable.class ) );

		final Iterable<?> printableInjectables = provider.dependencies.get( Printable.class );
		grantThatRetrieveAWellImplementedPrintableInstanceAsExpected();
		assertEquals( printableInjectables, provider.dependencies.get( Printable.class ) );
	}

	private void grantThatRetrieveAllClassesThatImplementsAnInterface() {
		final Iterable<Class<Printable>> implementations = provider.injectionContext.loadClassesImplementing( Printable.class );
		for ( final Class<Printable> clazz : implementations )
			if ( PrintableHello.class.equals(clazz) )
				return;
		fail( "Expected to find a Printable implementation." );
	}

	private void grantThatRetrieveAWellImplementedPrintableInstanceAsExpected() throws ServiceProviderException {
		final Printable printable = provider.load( Printable.class );
		assertThat( printable.toString(), is( "Hello World." ) );
	}
}
