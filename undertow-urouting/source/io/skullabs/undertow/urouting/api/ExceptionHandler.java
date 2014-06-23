package io.skullabs.undertow.urouting.api;

public interface ExceptionHandler<E extends Throwable> {

	Response handle( E exception );
}