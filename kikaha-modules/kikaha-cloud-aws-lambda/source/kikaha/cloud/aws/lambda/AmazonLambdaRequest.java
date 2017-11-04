package kikaha.cloud.aws.lambda;

import java.util.*;
//import com.fasterxml.jackson.annotation.*;
import io.undertow.UndertowMessages;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.*;
import kikaha.core.cdi.helpers.TinyList;
import lombok.*;

/**
 * With the Lambda proxy integration, API Gateway maps the entire client request to the
 * input event parameter of the back-end Lambda function as defined on this class.
 */
@ToString(exclude = "cookies")
//@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unchecked")
public class AmazonLambdaRequest {

    @Setter @Getter String resource;
    @Setter @Getter String path;
    @Setter @Getter String httpMethod;
    @Setter @Getter Map<String, String> headers;
    @Setter @Getter Map<String, String> queryStringParameters;
    @Setter @Getter Map<String, String> pathParameters;
    @Setter @Getter Map<String, String> stageVariables;
    @Setter @Getter RequestContext requestContext;
    @Setter @Getter String body;
    @Setter @Getter boolean isBase64Encoded;

//	@JsonIgnore
	@Getter(lazy = true)
	private final Map<String, Cookie> cookies = parseCookies();

//    @JsonIgnore
    transient private Map<AttachmentKey<?>, Object> attachments;

    /**
     * {@inheritDoc}
     */
//    @JsonIgnore
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
//    @JsonIgnoreProperties(ignoreUnknown = true)
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
//    @JsonIgnoreProperties(ignoreUnknown = true)
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
