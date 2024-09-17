package kikaha.cloud.aws.lambda;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import lombok.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link AmazonAuthenticationMechanism}.
 * Created by miere.teixeira on 03/08/2017.
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class AmazonAuthenticationMechanismTest {

    @Mock AmazonLambdaRequest request;
    @Mock AmazonLambdaResponse response;
    AmazonAuthenticationMechanism mechanism = spy( DummyMechanism.class );

    @Test
    public void canReadCredentialAndAuthenticate(){
        final UserAndPass userAndPass = new UserAndPass("ABC", "def");
        doReturn( userAndPass ).when( mechanism ).readCredentialFrom( eq(request) );
        doReturn( true ).when( mechanism ).authenticate( eq(userAndPass) );
        mechanism.validateRequest( request );
        verify( mechanism ).sendAuthenticationSuccess( eq(request) );
    }

    @Test
    public void canReadCredentialAndAuthenticateAndSendASuccessMessage(){
        final UserAndPass userAndPass = new UserAndPass("ABC", "def");
        doReturn( userAndPass ).when( mechanism ).readCredentialFrom( eq(request) );
        doReturn( true ).when( mechanism ).authenticate( eq(userAndPass) );
        doReturn( response ).when( mechanism ).sendAuthenticationSuccess( eq(request) );
        try {
            mechanism.validateRequest(request);
            fail( "It should interrupt the execution when there is a response to send" );
        } catch ( AmazonLambdaFunctionInterruptedException c ) {
            verify(mechanism).sendAuthenticationSuccess(eq(request));
        }
    }

    @Test
    public void cannotAuthenticateWhenNoCredentialWasFound(){
        doReturn( null ).when( mechanism ).readCredentialFrom( eq(request) );
        try {
            mechanism.validateRequest(request);
            fail( "It should interrupt the execution when there is a response to send" );
        } catch ( AmazonLambdaFunctionInterruptedException c ) {
            verify(mechanism, never()).authenticate(any());
            verify(mechanism, never()).sendAuthenticationSuccess(any());
            verify(mechanism).sendAuthenticationChallenge(eq(request));
        }
    }

    @Test
    public void canReadCredentialAndButNotAuthenticate(){
        final UserAndPass userAndPass = new UserAndPass("ABC", "def");
        doReturn( userAndPass ).when( mechanism ).readCredentialFrom( eq(request) );
        doReturn( false ).when( mechanism ).authenticate( eq(userAndPass) );
        try {
            mechanism.validateRequest(request);
            fail( "It should interrupt the execution when there is a response to send" );
        } catch ( AmazonLambdaFunctionInterruptedException c ) {
            verify(mechanism, never()).sendAuthenticationSuccess(any());
            verify(mechanism).sendAuthenticationChallenge(eq(request));
        }
    }
}

@RequiredArgsConstructor
class UserAndPass {
    final String user;
    final String pass;
}

class DummyMechanism implements AmazonAuthenticationMechanism {

    @Override
    public Object readCredentialFrom(AmazonLambdaRequest request) {
        return null;
    }

    @Override
    public boolean authenticate(Object credential) {
        return false;
    }
}