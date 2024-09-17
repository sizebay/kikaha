package kikaha.core.modules;

import io.undertow.Undertow;
import kikaha.core.DeploymentContext;

import java.io.IOException;

/**
 * Defines a module that should be executed when the application is started or
 * shutdown.
 */
public interface Module {

	/**
	 * Retrieve the module name. It is useful for module ordering proposes only.
	 *
	 * @return the module name.
	 */
	default String getName() {
		return "unnamed";
	}

	/**
	 * Event executed when server is initializing. This event allow developers to enhance
	 * the current application.<br>
	 * <br>
	 * The {@code context} instance allows deploy {@link io.undertow.server.HttpHandler}s,
	 * {@link kikaha.core.modules.smart.Filter}s or other Smart Route.<br>
	 * <br>
	 * The {@code server} instance allow developers to customize the {@link Undertow} server
	 * before it is started.
	 *
	 * @param server the server builder instance
	 * @param context the deployment context instance
	 * @throws IOException
	 */
	void load( Undertow.Builder server, DeploymentContext context ) throws IOException;

	/**
	 * Event executed when the server is shutdown. Allow developers to execute
	 * graceful-shutdown routines avoiding data loss when the server needs to shutdown.
	 */
	default void unload() throws IOException {}
}
