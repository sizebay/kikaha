package kikaha.core.modules;

import io.undertow.Undertow;
import kikaha.core.DeploymentContext;

import java.io.IOException;

/**
 *
 */
public interface Module {

	String getName();

	void load( Undertow.Builder server, DeploymentContext context ) throws IOException;

}
