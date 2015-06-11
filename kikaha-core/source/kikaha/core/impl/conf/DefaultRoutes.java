package kikaha.core.impl.conf;

import java.util.ArrayList;
import java.util.List;

import kikaha.core.api.conf.RewritableRule;
import kikaha.core.api.conf.Routes;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;

@Getter
@Accessors( fluent = true )
public class DefaultRoutes implements Routes {

	final List<RewritableRule> rewriteRoutes;
	final List<RewritableRule> reverseProxyRoutes;
	final String responseEncoding;
	final String requestEncoding;
	final Config config;

	public DefaultRoutes( final Config config ) {
		this.config = config;
		this.requestEncoding = config.getString("request-encoding");
		this.responseEncoding = config.getString("response-encoding");
		this.rewriteRoutes = parseRewritableRuleAtPath( "rewrite" );
		this.reverseProxyRoutes = parseRewritableRuleAtPath( "reverse" );
	}

	List<RewritableRule> parseRewritableRuleAtPath( final String path )
	{
		val defaultRewriteRoute = config().getConfig( "default-rewrite-rule" );
		val routes = new ArrayList<RewritableRule>();
		for ( val config : config().getConfigList( path ) ) {
			val rewriteRoute = createRewriteRoute( defaultRewriteRoute, config );
			routes.add( rewriteRoute );
		}
		return routes;
	}

	DefaultRewritableRoute createRewriteRoute( final Config defaultRewriteRoute, final Config config )
	{
		val rewriteConfig = config.withFallback( defaultRewriteRoute );
		return new DefaultRewritableRoute(
			rewriteConfig.getString( "virtual-host" ),
			rewriteConfig.getString( "path" ),
			rewriteConfig.getString( "to" ) );
	}
}
