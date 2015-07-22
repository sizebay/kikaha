package kikaha.core.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.undertow.security.idm.Account;

@Getter
@RequiredArgsConstructor
public class OutcomeResponse {

	public final static OutcomeResponse NOT_ATTEMPTED = new OutcomeResponse( null, Outcome.NOT_ATTEMPTED );
	public final static OutcomeResponse NOT_AUTHENTICATED = new OutcomeResponse( null, Outcome.NOT_AUTHENTICATED );

	final Account account;
	final Outcome outcome;

	public enum Outcome {
		NOT_ATTEMPTED, NOT_AUTHENTICATED, AUTHENTICATED
	}
}