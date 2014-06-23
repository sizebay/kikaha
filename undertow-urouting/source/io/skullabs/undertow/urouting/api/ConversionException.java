package io.skullabs.undertow.urouting.api;


public class ConversionException extends RoutingException {

	private static final long serialVersionUID = 2695056089411684745L;

	public ConversionException( String message, Throwable cause ) {
		super( message, cause );
	}

	public ConversionException( String message ) {
		super( message );
	}

	public ConversionException( Throwable cause ) {
		super( cause );
	}
}
