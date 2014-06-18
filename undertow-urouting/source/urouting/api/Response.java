package urouting.api;

/**
 * Interface defining a object that handles a data that will
 * be rendered. There's no intention to be a multi-propose object
 * and no thread-safety support at all. It will doing fine in a
 * per-request scope implementation.
 */
public interface Response {

	Object entity();

	Integer statusCode();

	String encoding();

	String contentType();

	Iterable<Header> headers();
}
