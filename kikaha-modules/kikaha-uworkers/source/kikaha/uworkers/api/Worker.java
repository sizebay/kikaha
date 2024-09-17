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

	/**
	 * Defines if should use the "listener name" as external HTTP URI.
	 * Otherwise, the uWorkersAPI will try to create one external HTTP URI automatically.
	 * By default, it uses the Canonical Name of method annotated with @Worker annotation.
	 * For example, the following class will be exposed at "/{base-endpoint}/sample.MyWorkerClass.method".
	 * The "base-endpoint" will be replaced by the value defined at the
	 * "{@code server.uworkers.rest-api.base-endpoint}" entry of the configuration file.<br><br>
	 *
	 * <pre><code>
	 * package sample;
	 * import kikaha.uworkers.api.Worker.
	 *
	 * class MyWorkerClass {
	 *    {@code @Worker("name")}
	 *     void method( Exchange exchange ){
	 *         exchange.reply("OK");
	 *     }
	 * }
	 * </code></pre>
	 *
	 *
	 *
	 * @return
	 */
	boolean useNameAsHttpURI() default false;
}
