package kikaha.core.modules.security;

/**
 *
 */
public class NotAuthorizedException extends RuntimeException {

	public NotAuthorizedException( Throwable cause ) {
		super(cause);
	}
}
