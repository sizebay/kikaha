package kikaha.core.api;

public class KikahaException extends Exception {

	private static final long serialVersionUID = 2747869622010963495L;

	public KikahaException( Throwable cause ) {
		super( cause );
	}

	public KikahaException( String message, Throwable cause ) {
		super( message, cause );
	}

	public KikahaException( String message ) {
		super( message );
	}
}
