package kikaha.core.modules;

import io.undertow.Undertow;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.reverse;

/**
 * Load and manage {@link Module}s life cycle.
 */
@Slf4j
@Singleton
public class ModuleLoader {

	@Inject
	@Typed( Module.class )
	Iterable<Module> modules;

	@Inject
	Config config;

	Map<String, List<Module>> modulesIndexedByName;
	List<String> enabledModules;

	@PostConstruct
	public void memorizeShutdownHooks(){
		Runtime.getRuntime().addShutdownHook( new Thread( this::unloadModules ) );
	}

	public void load( Undertow.Builder builder, DeploymentContext context ) throws IOException {
		log.info( "Looking for modules..." );
		loadModulesConfigurations();
		loadIndexedModules(builder, context);
		loadUnindexedModules(builder, context);
	}

	private void loadModulesConfigurations(){
		modulesIndexedByName = modulesIndexedByName();
		enabledModules = config.getStringList("server.enabled-modules");
		reverse(enabledModules);
	}

	private void loadIndexedModules( Undertow.Builder builder, DeploymentContext context ) throws IOException {
		for (String moduleName : this.enabledModules) {
			final List<Module> modules = modulesIndexedByName.remove( moduleName );
			if ( modules != null )
				for ( final Module module : modules ) {
					log.debug( "  Deploying module " + module.getClass().getCanonicalName() + "(name=" + moduleName + ")..." );
					module.load(builder, context);
				}
		}
	}

	private void loadUnindexedModules( Undertow.Builder builder, DeploymentContext context ) throws IOException {
		for (final List<Module> modules : modulesIndexedByName.values())
			for ( final Module module : modules )
				module.load( builder, context );
	}

	private Map<String, List<Module>> modulesIndexedByName(){
		final Map<String, List<Module>> index = new HashMap<>();
		for (Module module : modules)
			index.computeIfAbsent( module.getName(), k-> new ArrayList<>() ).add( module );
		return index;
	}

	private void unloadModules(){
		for ( Module module : modules )
			module.unload();
	}
}