package kikaha.core.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.security.idm.X509CertificateCredential;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class DefaultAdminOnlyIdentityManagerTest {

	final ServiceProvider provider = new ServiceProvider();
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
		val credential = new PasswordCredential( "t357".toCharArray() );
		val account = identityManager.verify( user, credential );
		assertNotNull( account );
		assertEquals( user, account.getPrincipal().getName() );
		assertTrue( account.getRoles().contains( "testable-role" ) );
	}

	@Test
	public void ensureThatCouldNotValidateAdminUser() {
		val user = "admin";
		val credential = new PasswordCredential( user.toCharArray() );
		val account = identityManager.verify( user, credential );
		assertNull( account );
	}

	@Test
	public void ensureThatInvalidCredentialsAreIgnored() {
		val admin = "admin";
		val credential = new X509CertificateCredential( null );
		val account = identityManager.verify( admin, credential );
		assertNull( account );
	}
}
