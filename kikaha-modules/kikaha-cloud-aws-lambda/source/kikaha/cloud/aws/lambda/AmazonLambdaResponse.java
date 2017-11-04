package kikaha.cloud.aws.lambda;

import java.util.*;
import io.undertow.util.Headers;
import kikaha.urouting.api.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 *
 */
@Getter @Setter @Accessors(chain = true)
@RequiredArgsConstructor
public class AmazonLambdaResponse {

	private static final Map<String, String> CONTENT_TYPE_JSON = Collections.singletonMap( Headers.CONTENT_TYPE_STRING, Mimes.JSON );

	int statusCode;
	Map<String, String> headers;
	String body;
	transient Object unserializedBody;

	final boolean isBase64Encoded = false;

    public void serializeAs(AmazonLambdaSerializer serializer ) {
        if ( unserializedBody != null ) {
            this.setBody(serializer.toString( unserializedBody ));
        }
    }

    public AmazonLambdaResponse setBody( String body ) {
        this.body = body;
        this.unserializedBody = null;
        return this;
    }

    public AmazonLambdaResponse setBody( Object body ) {
        this.unserializedBody = body;
        this.body = null;
        return this;
    }

	public static AmazonLambdaResponse with( Response response ) {
		final Map<String, String> headers = new HashMap<>();
		for ( final Header header : response.headers() )
			for ( final String value : headers.values() )
				headers.put( header.name().toString(), value );
		headers.put( Headers.CONTENT_TYPE_STRING, Mimes.JSON );

		final Object body = response.entity();
		return new AmazonLambdaResponse()
            .setStatusCode( response.statusCode() )
            .setHeaders( headers )
            .setBody( body );
	}

	public static AmazonLambdaResponse with( Object body ) {
		return new AmazonLambdaResponse()
            .setStatusCode(200)
            .setHeaders( CONTENT_TYPE_JSON )
            .setBody( body );
	}

	public static AmazonLambdaResponse noContent() {
		return AmazonLambdaResponse.create( 204 );
	}

	public static AmazonLambdaResponse notFound() {
		return AmazonLambdaResponse.create( 404 );
	}

    public static AmazonLambdaResponse notAuthenticated() {
		return AmazonLambdaResponse.create( 401 );
    }

    public static AmazonLambdaResponse create( int statusCode ) {
        return create( statusCode, Collections.emptyMap() );
    }

    public static AmazonLambdaResponse create( int statusCode, Map<String, String> headers ) {
        return new AmazonLambdaResponse()
            .setStatusCode( statusCode )
            .setHeaders( headers );
    }
}
