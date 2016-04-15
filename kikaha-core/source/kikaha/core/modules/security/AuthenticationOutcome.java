package kikaha.core.modules.security;

import lombok.RequiredArgsConstructor;

/**
 * The AuthenticationOutcome is used by an AuthenticationMechanism (or IdentityManager) to indicate the outcome of the call to authenticate, the
 * overall authentication process will then used this along with the current AuthenticationState to decide how to proceed
 * with the current request.
 */
@RequiredArgsConstructor
public enum AuthenticationOutcome {

    /**
     * Based on the current request the mechanism has successfully performed authentication.
     */
    AUTHENTICATED,

    /**
     * The mechanism attempted authentication but it did not complete, this could either be due to a failure validating the
     * tokens from the client or it could be due to the mechanism requiring at least one additional round trip with the
     * client - either way the request will return challenges to the client.
     */
    NOT_AUTHENTICATED,

    /**
     * The mechanism did not attempt authentication on this request, most likely due to not discovering any applicable
     * security tokens for this mechanisms in the request.
     */
    NOT_ATTEMPTED;
}