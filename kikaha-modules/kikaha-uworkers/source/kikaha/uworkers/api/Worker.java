package kikaha.uworkers.api;

import java.lang.annotation.*;

/**
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface Worker {

	/**
	 * Defines the value name this Worker is listen for messages.
	 * @return
	 */
	String value();
}
