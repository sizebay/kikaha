package kikaha.core.modules.security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;

@Ignore
@RunWith(KikahaRunner.class)
public class DefaultFormAuthenticationMechanismTest {

	@Inject
	AuthenticationEndpoints mechanism;

	@Test
	public void ensureThatWrappedUpAndFillUpFormAuthenticationMechanismFieldsAsExpected() {
		assertThat( mechanism.getLoginPage(), is( "/auth/" ) );
		assertThat( mechanism.getErrorPage(), is( "/auth/error/" ) );
		assertThat( mechanism.getCallbackUrl(), is( "/auth/callback" ) );
	}
}
