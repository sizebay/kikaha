package kikaha.urouting.api;

import io.undertow.util.*;

/**
 * Interface defining a object that handles a data that will
 * be rendered. There's no intention to be a multi-propose object
 * neither has thread-safety support at all. But it will doing fine in a
 * per-request scope implementation.
 */
public interface Response {

	/**
	 * Get the object that should be serialized.
	 *
	 * @return
	 */
	Object entity();

	/**
	 * Get the status code.
	 *
	 * @return
	 */
	int statusCode();

	/**
	 * Get the encoding.
	 *
	 * @return
	 */
	String encoding();

	/**
	 * Get the headers.
	 *
	 * @return
	 */
	Iterable<Header> headers();

	/**
	 * Retrieve headers named {@code name}.
	 *
	 * @param name
	 * @return
	 */
	default Header header( final String name ) {
		return header( new HttpString(name) );
	}

	/**
	 * Retrieve headers named {@code name}.
	 *
	 * @param name
	 * @return
	 */
	// XXX: good enough, but one may improve this... ;)
	default Header header( final HttpString name ) {
		for ( final Header header : headers() )
			if ( header.name().equals(name) )
				return header;
		return null;
	}

	/**
	 * Get the Content-Type defined with this {@link Response}.
	 *
	 * @return
	 */
	default String contentType(){
		final Header header = header( Headers.CONTENT_TYPE );
		return header!=null ? header.values().get( 0 ) : null;
	}
}
