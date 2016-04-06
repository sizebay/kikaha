package kikaha.core.modules;

import io.undertow.Undertow;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Singleton
public class ModuleLoader {

	@Inject
	@Typed( Module.class )
	Iterable<Module> modules;

	@Inject
	Config config;

	Map<String, Module> modulesIndexedByName;
	List<String> enabledModules;

	public void load( Undertow.Builder builder, DeploymentContext context ) throws IOException {
		loadModulesConfigurations();
		loadIndexedModules(builder, context);
		loadUnindexedModules(builder, context);
	}

	private void loadModulesConfigurations(){
		modulesIndexedByName = modulesIndexedByName();
		enabledModules = config.getStringList("server.enabled-modules");
	}

	private void loadIndexedModules( Undertow.Builder builder, DeploymentContext context ) throws IOException {
		for (String module : enabledModules) {
			final Module enabledModule = modulesIndexedByName.remove( module );
			if ( enabledModule != null )
				enabledModule.load( builder, context );
		}
	}

	private void loadUnindexedModules( Undertow.Builder builder, DeploymentContext context ) throws IOException {
		for (Module module : modulesIndexedByName.values())
			module.load( builder, context );
	}

	private Map<String, Module> modulesIndexedByName(){
		final Map<String, Module> index = new HashMap<>();
		for (Module module : modules)
			index.put( module.getName(), module );
		return index;
	}
}
