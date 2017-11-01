package kikaha.cloud.aws.lambda;

import java.util.*;
import com.fasterxml.jackson.annotation.*;
import io.undertow.UndertowMessages;
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
@ToString(exclude = "cookies")
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unchecked")
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

    @JsonIgnore
    private Map<AttachmentKey<?>, Object> attachments;

    /**
     * {@inheritDoc}
     */
    public <T> T getAttachment(final AttachmentKey<T> key) {
        if (key == null || attachments == null) {
            return null;
        }
        return (T) attachments.get(key);
    }

    public <T> T putAttachment(final AttachmentKey<T> key, final T value) {
        if (key == null) throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");

        if(attachments == null)
            attachments = createAttachmentMap();

        return (T) attachments.put(key, value);
    }

    private Map<AttachmentKey<?>, Object> createAttachmentMap() {
        return new IdentityHashMap<>(5);
    }

    public <T> T removeAttachment(final AttachmentKey<T> key) {
        if (key == null || attachments == null) {
            return null;
        }
        return (T) attachments.remove(key);
    }

    private Map<String, Cookie> parseCookies() {
		final String cookie = headers.get( Headers.COOKIE_STRING );
		if (cookie == null)
			return Collections.emptyMap();
		return Cookies.parseRequestCookies( 50, false, TinyList.singleElement( cookie ) );
	}

	@Getter
	@Setter
	@ToString
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
	@ToString
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
