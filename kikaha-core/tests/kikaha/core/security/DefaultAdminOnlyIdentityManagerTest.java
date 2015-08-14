package kikaha.core.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.undertow.security.idm.X509CertificateCredential;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import kikaha.core.security.FixedUserAndPasswordIdentityManager;
import kikaha.core.security.IdentityManager;
import kikaha.core.security.UsernameAndPasswordCredential;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class DefaultAdminOnlyIdentityManagerTest {

	final ServiceProvider provider = new DefaultServiceProvider();
	IdentityManager identityManager;

	@Before
	public void setupIdentityManager() throws ServiceProviderException {
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		identityManager = new FixedUserAndPasswordIdentityManager();
		provider.provideOn( identityManager );
	}

	@Test
	public void ensureThatCouldValidateTestUserAsDefinedInApplicationConf() {
		val user = "test";
		val credential = new UsernameAndPasswordCredential( user, "t357" );
		val account = identityManager.verify( credential );
		assertNotNull( account );
		assertEquals( user, account.getPrincipal().getName() );
		assertTrue( account.getRoles().contains( "testable-role" ) );
	}

	@Test
	public void ensureThatCouldNotValidateAdminUser() {
		val user = "admin";
		val credential = new UsernameAndPasswordCredential( user, user );
		val account = identityManager.verify( credential );
		assertNull( account );
	}

	@Test
	public void ensureThatInvalidCredentialsAreIgnored() {
		val credential = new X509CertificateCredential( null );
		val account = identityManager.verify( credential );
		assertNull( account );
	}
}
