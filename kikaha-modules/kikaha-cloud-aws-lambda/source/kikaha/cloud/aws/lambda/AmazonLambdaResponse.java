package kikaha.cloud.aws.lambda;

import java.util.*;
import io.undertow.util.Headers;
import kikaha.urouting.api.*;
import lombok.*;

/**
 *
 */
@Getter
@RequiredArgsConstructor
public class AmazonLambdaResponse {

	private static final Map<String, String> CONTENT_TYPE_JSON = Collections.singletonMap( Headers.CONTENT_TYPE_STRING, Mimes.JSON );

	final int statusCode;
	final Map<String, String> headers;
	final String body;

	final boolean isBase64Encoded = false;

	public static AmazonLambdaResponse with( Response response ) {
		final Map<String, String> headers = new HashMap<>();
		for ( final Header header : response.headers() )
			for ( final String value : headers.values() )
				headers.put( header.name().toString(), value );
		headers.put( Headers.CONTENT_TYPE_STRING, Mimes.JSON );

		final Object body = response.entity();
		return new AmazonLambdaResponse( response.statusCode(), headers,
				body == null ? "null" : Jackson.toJsonString( body ) );
	}

	public static AmazonLambdaResponse with( Object body ) {
		return new AmazonLambdaResponse( 200, CONTENT_TYPE_JSON,
			body == null ? "null" : Jackson.toJsonString( body )
		);
	}

	public static AmazonLambdaResponse noContent() {
		return new AmazonLambdaResponse( 204, Collections.emptyMap(), null );
	}

	public static AmazonLambdaResponse notFound() {
		return new AmazonLambdaResponse( 404, Collections.emptyMap(), null );
	}

    public static AmazonLambdaResponse notAuthenticated() {
		return new AmazonLambdaResponse( 401, Collections.emptyMap(), null );
    }
}
