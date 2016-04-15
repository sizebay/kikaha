package kikaha.core.modules.security;

import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(KikahaRunner.class)
public class DefaultFormAuthenticationMechanismTest {

	@Inject
	FormAuthenticationMechanism mechanism;

	@Test
	public void ensureThatWrappedUpAndFillUpFormAuthenticationMechanismFieldsAsExpected() {
		assertThat( mechanism.getLoginPage(), is( "/auth/" ) );
		assertThat( mechanism.getErrorPage(), is( "/auth/error/" ) );
		assertThat( mechanism.getPostLocation(), is( "j_security_check" ) );
	}
}
