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
	 * Defines the endpoint name this Worker is listen for messages.
	 * @return
	 */
	String endpoint();

	/**
	 * It defines the configuration alias used to configure how worker should behave. It
	 * should contains only letters or numbers, no special character will be allowed here.
	 * If not set, it will use the "default" alias to look for configurations.
	 * @return
	 */
	String alias() default "default";
}
