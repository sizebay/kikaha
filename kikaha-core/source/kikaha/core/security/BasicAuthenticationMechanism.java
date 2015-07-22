package kikaha.core.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import kikaha.core.security.OutcomeResponse.Outcome;
import kikaha.core.url.StringCursor;
import lombok.extern.slf4j.Slf4j;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.FlexBase64;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;

@Slf4j
public class BasicAuthenticationMechanism implements AuthenticationMechanism {

	private static final String BASIC_PREFIX = Headers.BASIC + " ";
	private static final int PREFIX_LENGTH = BASIC_PREFIX.length();
	private static final char COLON = ':';
	private final String challenge;

	public BasicAuthenticationMechanism() {
		this( "default" );
	}

	public BasicAuthenticationMechanism( String realmName ) {
		this.challenge = BASIC_PREFIX + "realm=\"" + realmName + "\"";
	}

	@Override
	public OutcomeResponse authenticate( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers ) {
		final StringCursor decodedCredentials = getDecodedCredentialsFromHeader( exchange );
		if ( decodedCredentials == null )
			return OutcomeResponse.NOT_ATTEMPTED;
		final UsernameAndPasswordCredential credential = convertToCredential( decodedCredentials );
		final Account account = verify( identityManagers, credential );
		final Outcome outcome = account != null ? Outcome.AUTHENTICATED : Outcome.NOT_AUTHENTICATED;
		return new OutcomeResponse( account, outcome );
	}

	private UsernameAndPasswordCredential convertToCredential( StringCursor decodedCredentials ) {
		if ( decodedCredentials.shiftCursorToNextChar( COLON ) )
			decodedCredentials.mark();
		final String username = decodedCredentials.substringFromLastMark();
		decodedCredentials.mark();
		decodedCredentials.end();
		final String password = decodedCredentials.substringFromLastMark();
		UsernameAndPasswordCredential credential = new UsernameAndPasswordCredential( username, password );
		return credential;
	}

	private StringCursor getDecodedCredentialsFromHeader( HttpServerExchange exchange ) {
		StringCursor decodedCredentials = null;
		final StringCursor headerValue = getAuthenticationHeader( exchange );
		if ( headerValue != null ) {
			String authString = getAuthString( headerValue );
			decodedCredentials = decode( authString );
		}
		return decodedCredentials;
	}

	private StringCursor decode( String authString ) {
		try {
			final ByteBuffer decode = FlexBase64.decode( authString );
			final String string = new String( decode.array(), decode.arrayOffset(), decode.limit(), StandardCharsets.UTF_8 );
			StringCursor decodedCredentials = new StringCursor( string );
			return decodedCredentials;
		} catch ( IOException cause ) {
			log.warn( "Ignoring exception during Base64 decoding.", cause );
			return null;
		}
	}

	private String getAuthString( StringCursor headerValue ) {
		headerValue.shiftCursorToNextChar( ' ' );
		headerValue.mark();
		headerValue.end();
		return headerValue.substringFromLastMark();
	}

	private StringCursor getAuthenticationHeader( HttpServerExchange exchange ) {
		final HeaderValues authHeaders = exchange.getRequestHeaders().get( Headers.AUTHORIZATION );
		StringCursor headerValue = null;
		for ( String value : authHeaders )
			if ( value.startsWith( BASIC_PREFIX ) )
				headerValue = new StringCursor( value );
		return headerValue;
	}
}
