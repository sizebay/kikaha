package kikaha.core.security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import io.undertow.security.api.AuthenticationMechanism;
import kikaha.core.api.conf.AuthenticationRuleConfiguration;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class DefaultFormAuthenticationMechanismTest {

	ServiceProvider provider;
	FormAuthenticationMechanism mechanism;

	@Mock
	AuthenticationMechanism mockedMechanism;

	@Mock
	AuthenticationRuleConfiguration config;

	/**
	 * Ensure that wrapped up and fill up FormAuthenticationMechanism fields as
	 * expected
	 *
	 * @throws ServiceProviderException
	 */
	@Test
	public void ensureThatWrappedUpAndFillUpFormAuthenticationMechanismFieldsAsExpected()
		throws ServiceProviderException {
		assertThat( mechanism.getLoginPage(), is( "/auth/" ) );
		assertThat( mechanism.getErrorPage(), is( "/auth/error/" ) );
		assertThat( mechanism.getPostLocation(), is( "j_security_check" ) );
	}

	@SneakyThrows
	String getAttributeAsString( final Object object, final String fieldName ) {
		final Class<? extends Object> clazz = object.getClass();
		return getAttributeAsString( object, fieldName, clazz );
	}

	String getAttributeAsString( final Object object, final String fieldName, final Class<?> clazz ) throws IllegalAccessException {
		try {
			val field = clazz.getDeclaredField( fieldName );
			field.setAccessible( true );
			return (String)field.get( object );
		} catch ( final NoSuchFieldException cause ) {
			final Class<?> superclass = clazz.getSuperclass();
			if ( superclass.equals( object.getClass() ) )
				return null;
			return getAttributeAsString( object, fieldName, superclass );
		}
	}

	@Before
	public void provideDependencies() throws ServiceProviderException {
		MockitoAnnotations.initMocks( this );
		provider = new DefaultServiceProvider();
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		mechanism = createFormMechanismWrapper();
		doReturn( "/auth/*" ).when( config ).pattern();
	}

	FormAuthenticationMechanism createFormMechanismWrapper()
		throws ServiceProviderException {
		val wrapper = new FormAuthenticationMechanism();
		provider.provideOn( wrapper );
		return spy( wrapper );
	}
}
