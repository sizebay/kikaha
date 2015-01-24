package kikaha.core.impl.conf;

import java.util.ArrayList;
import java.util.List;

import kikaha.core.api.conf.RewriteRoute;
import kikaha.core.api.conf.Routes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class DefaultRoutes implements Routes {

	final Config config;

	@Getter( lazy = true )
	private final List<RewriteRoute> rewriteRoutes = parseRewriteRoutes();

	List<RewriteRoute> parseRewriteRoutes()
	{
		val defaultRewriteRoute = config().getConfig( "default-rewrite-rule" );
		val routes = new ArrayList<RewriteRoute>();
		for ( val config : config().getConfigList( "rewrite" ) ) {
			val rewriteRoute = createRewriteRoute( defaultRewriteRoute, config );
			routes.add( rewriteRoute );
		}

		return routes;
	}

	DefaultRewriteRoute createRewriteRoute( final Config defaultRewriteRoute, final Config config )
	{
		val rewriteConfig = config.withFallback( defaultRewriteRoute );
		return new DefaultRewriteRoute(
			rewriteConfig.getString( "virtual-host" ),
			rewriteConfig.getString( "path" ),
			rewriteConfig.getString( "to" ) );
	}
}
