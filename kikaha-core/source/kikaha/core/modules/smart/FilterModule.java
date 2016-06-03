package kikaha.core.modules.smart;

import java.io.IOException;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import javax.inject.*;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.helpers.TinyList;
import kikaha.core.modules.Module;
import lombok.Getter;

/**
 * A {@link Module} that deploys {@link Filter}s.
 */
@Getter
@Singleton
public class FilterModule implements Module {

	final String name = "filter";

	@Inject
	Config config;

	@Inject
	@Typed( Filter.class )
	Collection<Filter> foundFilters;

	@PostConstruct
	public void loadConfiguredFilters(){
		for ( Config filterConf : config.getConfigList( "server.smart-routes.filter" ) ) {
			final Filter filter = RedirectionFilter.from( SmartRouteRule.from(filterConf) );
			foundFilters.add( filter );
		}
	}

	@Override
	public void load( Undertow.Builder server, DeploymentContext context ) throws IOException {
		final List<Filter> filterList = new TinyList<>();
		filterList.addAll(foundFilters);

		if ( filterList.isEmpty() )
			return;

		final FilterHttpHandler filterHttpHandler = createTheFilterHttpHandler(context, filterList);
		context.rootHandler( filterHttpHandler );
	}

	FilterHttpHandler createTheFilterHttpHandler( DeploymentContext context, final List<Filter> filterList ) {
		final HttpHandler httpHandler = context.rootHandler();
		filterList.add( new HttpHandlerRunnerFilter(httpHandler) );

		final FilterChainFactory chainFactory = new FilterChainFactory(filterList);
		return new FilterHttpHandler(chainFactory);
	}
}
