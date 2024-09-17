package kikaha.urouting.api;

import io.undertow.util.*;
import lombok.NonNull;

/**
 *
 */
public interface MutableResponse extends Response {

	/**
	 * Define the entity that will be serialized.
	 *
	 * @param entity
	 * @return
	 */
	MutableResponse entity( Object entity );

	/**
	 * Override all headers.
	 *
	 * @param headers
	 * @return
	 */
	MutableResponse headers( Iterable<Header> headers );

	/**
	 * Define the content encoding.
	 *
	 * @param encoding
	 * @return
	 */
	MutableResponse encoding( String encoding );

	/**
	 * Define the Content-Type defined with this {@link Response}.
	 *
	 * @param value
	 * @return
	 */
	default MutableResponse contentType( String value ) {
		return header( Headers.CONTENT_TYPE, value );
	}

	/**
	 * Defines a new header value.
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	default MutableResponse header(final String name, @NonNull final String value ) {
		return header( new HttpString(name), value );
	}

	/**
	 * Defines a new header value.
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	MutableResponse header(final HttpString name, final String value );
}
