package kikaha.urouting.api;

public class RoutingException extends Exception {

	private static final long serialVersionUID = -3572128955736929846L;

	public RoutingException( String message, Throwable cause ) {
		super( message, cause );
	}

	public RoutingException( String message ) {
		super( message );
	}

	public RoutingException( Throwable cause ) {
		super( cause );
	}
}
