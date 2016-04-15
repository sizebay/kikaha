package kikaha.core.modules.security;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.FlexBase64;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import kikaha.core.url.StringCursor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@Singleton
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
	public Account authenticate( HttpServerExchange exchange, Iterable<IdentityManager> identityManagers, Session session ) {
		final StringCursor decodedCredentials = getDecodedCredentialsFromHeader( exchange );
		if ( decodedCredentials == null )
			return null;
		final UsernameAndPasswordCredential credential = convertToCredential( decodedCredentials );
		return verify( identityManagers, credential );
	}

	private UsernameAndPasswordCredential convertToCredential( StringCursor decodedCredentials ) {
		decodedCredentials.shiftCursorToNextChar( COLON );
		final String username = decodedCredentials.substringFromLastMark(1);
		decodedCredentials.mark();
		decodedCredentials.end();
		final String password = decodedCredentials.substringFromLastMark();
		return new UsernameAndPasswordCredential( username, password );
	}

	private StringCursor getDecodedCredentialsFromHeader( HttpServerExchange exchange ) {
		StringCursor decodedCredentials = null;
		final StringCursor headerValue = getAuthenticationHeader( exchange );
		if ( headerValue != null ) {
			final String authString = getAuthString( headerValue );
			decodedCredentials = decode( authString );
		}
		return decodedCredentials;
	}

	private StringCursor decode( String authString ) {
		try {
			final ByteBuffer decode = FlexBase64.decode( authString );
			final String string = new String( decode.array(), decode.arrayOffset(), decode.limit(), StandardCharsets.UTF_8 );
			final StringCursor decodedCredentials = new StringCursor( string );
			return decodedCredentials;
		} catch ( final IOException cause ) {
			log.warn( "Ignoring exception during Base64 decoding.", cause );
			return null;
		}
	}

	private String getAuthString( StringCursor headerValue ) {
		headerValue.cursorAt( PREFIX_LENGTH );
		headerValue.mark();
		headerValue.end();
		return headerValue.substringFromLastMark();
	}

	private StringCursor getAuthenticationHeader( HttpServerExchange exchange ) {
		final HeaderValues authHeaders = exchange.getRequestHeaders().get( Headers.AUTHORIZATION );
		StringCursor headerValue = null;
		if( authHeaders != null )
			for ( final String value : authHeaders )
				if ( value.startsWith( BASIC_PREFIX ) )
					headerValue = new StringCursor( value );
		return headerValue;
	}

	@Override
	public boolean sendAuthenticationChallenge(HttpServerExchange exchange, Session session) {
		exchange.setResponseCode( StatusCodes.UNAUTHORIZED );
		exchange.getResponseHeaders().add( Headers.WWW_AUTHENTICATE, challenge );
		return true;
	}
}
