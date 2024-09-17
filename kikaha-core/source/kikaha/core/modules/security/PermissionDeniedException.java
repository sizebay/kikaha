package kikaha.core.modules.security;

/**
 *
 */
public class PermissionDeniedException extends RuntimeException {

	public PermissionDeniedException( Throwable cause ) {
		super(cause);
	}
}
