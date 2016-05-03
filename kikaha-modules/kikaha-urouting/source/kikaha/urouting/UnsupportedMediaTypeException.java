package kikaha.urouting;

import java.io.IOException;

public class UnsupportedMediaTypeException extends IOException {

	private static final long serialVersionUID = -3572128955736929846L;

	public UnsupportedMediaTypeException(final String message, final Throwable cause ) {
		super( message, cause );
	}

	public UnsupportedMediaTypeException(final String message ) {
		super( message );
	}

	public UnsupportedMediaTypeException(final Throwable cause ) {
		super( cause );
	}
}
