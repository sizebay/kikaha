package kikaha.cloud.aws.lambda;

import java.util.*;
import com.fasterxml.jackson.annotation.*;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.*;
import kikaha.core.cdi.helpers.TinyList;
import lombok.*;

/**
 * With the Lambda proxy integration, API Gateway maps the entire client request to the
 * input event parameter of the back-end Lambda function as defined on this class.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmazonLambdaRequest {

	String resource;
	String path;
	String httpMethod;
	Map<String, String> headers;
	Map<String, String> queryStringParameters;
	Map<String, String> pathParameters;
	Map<String, String> stageVariables;
	RequestContext requestContext;
	String body;
	boolean isBase64Encoded;

	@JsonIgnore
	@Getter(lazy = true)
	private final Map<String, Cookie> cookies = parseCookies();

	private Map<String, Cookie> parseCookies() {
		final String cookie = headers.get( Headers.COOKIE_STRING );
		if (cookie == null)
			return Collections.emptyMap();
		return Cookies.parseRequestCookies( 50, false, TinyList.singleElement( cookie ) );
	}

	@Getter
	@Setter
	public static class RequestContext {
		String accountId;
		String resourceId;
		String stage;
		String requestId;
		RequestContextIdentity identity;
		String resourcePath;
		String httpMethod;
		String apiId;
	}

	@Getter
	@Setter
	public static class RequestContextIdentity {
		String cognitoIdentityPoolId;
		String accountId;
		String cognitoIdentityId;
		String caller;
		String apiKey;
		String sourceIp;
		String cognitoAuthenticationType;
		String cognitoAuthenticationProvider;
		String userArn;
		String userAgent;
		String user;
	}
}
