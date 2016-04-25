package kikaha.cdi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import javax.enterprise.inject.Produces;

import kikaha.cdi.tests.ann.Ajax;
import kikaha.cdi.tests.ann.DarkKnight;
import kikaha.cdi.tests.ann.Foo;
import kikaha.core.cdi.DefaultServiceProvider;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.cdi.ServiceProviderException;
import kikaha.core.cdi.helpers.filter.QualifierCondition;

import org.junit.Test;

public class GeneratedCodeAndMetaINFTest {

	final DefaultServiceProvider provider = new DefaultServiceProvider();

	@Test
	public void grantThatCouldRetrieveAjaxFromMars() throws ServiceProviderException {
		final Hero hero = this.provider.load( Hero.class, qualifier( Ajax.class ) );
		assertNotNull( "No 'Hero' implementations found", hero );
		assertEquals( "Expected 'Hero' should be 'AjaxFromMars' instance",
				hero.getClass(), AjaxFromMars.class );
		final AjaxFromMars ajax = (AjaxFromMars)hero;
		assertEquals( "'ajax' doesn't provide the expected string", "Mars", ajax.getWorld() );
	}

	@Test
	public void grantThatCouldRetrieveBatman() throws ServiceProviderException {
		final Hero hero = this.provider.load( Hero.class, qualifier( DarkKnight.class ) );
		assertNotNull( hero );
		assertEquals( Batman.class, hero.getClass() );
		final Batman batman = (Batman)hero;
		assertEquals( "'batman' doesn't provide the expected string", "Mars", batman.getWorld() );
	}

	<T> QualifierCondition<T> qualifier( Class<? extends Annotation> ann ) {
		return new QualifierCondition<>( Arrays.asList( ann ) );
	}

	@Produces
	public String produceAGenericString( final ProviderContext context ) {
		return null;
	}
}
