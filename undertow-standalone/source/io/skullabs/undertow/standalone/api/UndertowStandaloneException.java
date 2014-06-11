package io.skullabs.undertow.standalone.api;

public class UndertowStandaloneException extends Exception {

	private static final long serialVersionUID = 2747869622010963495L;

	public UndertowStandaloneException( Throwable cause ) {
		super( cause );
	}

	public UndertowStandaloneException( String message, Throwable cause ) {
		super( message, cause );
	}

	public UndertowStandaloneException( String message ) {
		super( message );
	}
}
