package kikaha.core.cdi;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import lombok.Getter;
import lombok.val;

import org.junit.Test;

public class SingleAndManyServicesInjectionTest {

	final ServiceProvider provider = new DefaultServiceProvider();

	@Test( timeout = 6000 )
	public void applyStressTestOnMyAssertion() throws ServiceProviderException {
		for ( int i = 0; i < 1000000; i++ )
			ensureThatHaveInjectedBothSingleAndManyElementsIntoInjectedClass();
	}

	@Test
	public void ensureThatHaveInjectedBothSingleAndManyElementsIntoInjectedClass()
			throws ServiceProviderException
	{
		val injectable = new InjectableClass();
		provider.provideOn( injectable );
		assertThat( injectable.getPrintableWord(), instanceOf( PrintableWorld.class ) );
		assertPrintablesArePopulatedAsExpected( injectable );
		assertPrintableFoosArePopulatedAsExpected( injectable );
	}

	void assertPrintablesArePopulatedAsExpected( final InjectableClass injectable ) {
		val printables = injectable.getPrintables();
		assertThat( printables, notNullValue() );
		assertContains( printables, PrintableFoo.class );
		assertContains( printables, PrintableWorld.class );
	}

	void assertPrintableFoosArePopulatedAsExpected( final InjectableClass injectable ) {
		val printableFoos = injectable.getPrintableFoos();
		assertContains( printableFoos, PrintableFoo.class );
		assertNotContains( printableFoos, PrintableWorld.class );
	}

	void assertContains( Iterable<?> iterable, Class<?> clazz ) {
		for ( val item : iterable )
			if ( item.getClass().equals( clazz ) )
				return;
		fail( "Iterable doesn't contains any object from type " + clazz.getCanonicalName() );
	}

	void assertNotContains( Iterable<?> iterable, Class<?> clazz ) {
		for ( val item : iterable )
			if ( item.getClass().equals( clazz ) )
				fail( "Iterable contains an object from type " + clazz.getCanonicalName() );
	}

	@Test
	public void ensureThatNonManagedClassesAreCreatedEveryTime() {
		final InjectableClass injectable = provider.load( InjectableClass.class );
		assertNotNull( injectable );
		assertPrintablesArePopulatedAsExpected( injectable );
		assertPrintableFoosArePopulatedAsExpected( injectable );
	}
}

@Getter
class InjectableClass {

	@Inject
	PrintableWord printableWord;

	@Inject
	@Typed( PrintableWord.class )
	Iterable<PrintableWord> printables;

	@Inject
	@Foo
	@Typed( PrintableWord.class )
	Iterable<PrintableWord> printableFoos;
}