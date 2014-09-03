package kikaha.core.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class DefaultFormAuthenticationMechanismTest {

	ServiceProvider provider;
	DefaultFormAuthenticationMechanism wrapper;

	@Mock
	AuthenticationMechanism mockedMechanism;

	/**
	 * Ensure that wrapped up and fill up FormAuthenticationMechanism fields as
	 * expected
	 * 
	 * @throws ServiceProviderException
	 */
	@Test
	public void ensureThatWrappedUpAndFillUpFormAuthenticationMechanismFieldsAsExpected()
		throws ServiceProviderException {
		val mechanism = wrapper.getMechanism();
		assertThat( getAttributeAsString( mechanism, "name" ), is( "DefaultFormAuth" ) );
		assertThat( getAttributeAsString( mechanism, "loginPage" ), is( "/auth/" ) );
		assertThat( getAttributeAsString( mechanism, "errorPage" ), is( "/auth/error/" ) );
		assertThat( getAttributeAsString( mechanism, "postLocation" ), is( "/auth/j_security_check" ) );
	}

	/**
	 * Ensure that delegated AuthenticationMechanism methods to wrapped object.
	 */
	@Test
	public void ensureThatDelegatedAuthenticationMechanismMethodsToWrappedObject() {
		doReturn( mockedMechanism ).when( wrapper ).createFormAuthMechanism();
		wrapper.authenticate( null, null );
		verify( mockedMechanism ).authenticate( any( HttpServerExchange.class ), any( SecurityContext.class ) );
		wrapper.sendChallenge( null, null );
		verify( mockedMechanism ).sendChallenge( any( HttpServerExchange.class ), any( SecurityContext.class ) );
	}

	@SneakyThrows
	String getAttributeAsString( Object object, String fieldName ) {
		val field = object.getClass().getDeclaredField( fieldName );
		field.setAccessible( true );
		return (String)field.get( object );
	}

	@Before
	public void provideDependencies() throws ServiceProviderException {
		MockitoAnnotations.initMocks( this );
		provider = new ServiceProvider();
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		wrapper = createFormMechanismWrapper();
	}

	DefaultFormAuthenticationMechanism createFormMechanismWrapper()
		throws ServiceProviderException {
		val wrapper = new DefaultFormAuthenticationMechanism();
		provider.provideOn( wrapper );
		return spy( wrapper );
	}
}
