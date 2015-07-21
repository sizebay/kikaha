package kikaha.core.security;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface IdentityManager {

    /**
     * Perform verification when all we have is the Credential, in this case the IdentityManager is also responsible for mapping the Credential to an account.
     *
     * @param credential
     * @return
     */
	VerificationResponse verify(final Credential credential);

    @Getter
    @RequiredArgsConstructor
    public class VerificationResponse {
    	final AuthenticationOutcome outcome;
    	final Account account;
    }
}
