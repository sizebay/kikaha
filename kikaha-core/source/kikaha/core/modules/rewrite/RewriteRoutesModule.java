package kikaha.core.modules.rewrite;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Singleton
public class RewriteRoutesModule implements Module {

	final String name = "rewrite";

	@Inject
	Config config;

	List<RewritableRule> rewriteRoutes;

	List<RewritableRule> reverseRoutes;

	boolean enabledHttpsAutoRedirect;

	@PostConstruct
	public void loadConfig(){
		List<Config> configs = config.getConfigList("server.routes.rewrite");
		rewriteRoutes = configs.stream().map( c->RewritableRule.from(c) ).collect(Collectors.toList());
		configs = config.getConfigList("server.routes.reverse");
		reverseRoutes = configs.stream().map( c->RewritableRule.from(c) ).collect(Collectors.toList());
		enabledHttpsAutoRedirect = config.getBoolean("server.routes.auto-redirect-http-to-https");
	}

	public void load(final Undertow.Builder server, final DeploymentContext context )
	{
		deployRewriteRoutes( context );
		deployReverseProxyRoutes( context );
		deployHttpToHttps( context );
	}

	private void deployRewriteRoutes( final DeploymentContext context )
	{
		for ( RewritableRule route : rewriteRoutes ) {
			log.info( "Rewrite rule: " + route );
			final HttpHandler rewriteHandler = RewriteRequestHttpHandler.from( route, context.rootHandler() );
			context.rootHandler( rewriteHandler );
		}
	}

	private void deployReverseProxyRoutes( final DeploymentContext context )
	{
		HttpHandler lastHandler = context.rootHandler();
		for ( RewritableRule route : reverseRoutes ) {
			log.info( "Reverse proxy rule: " + route );
			final ProxyClient proxyClient = RewriterProxyClientProvider.from( route );
			lastHandler = new ProxyHandler( proxyClient, lastHandler );
		}
		context.rootHandler( lastHandler );
	}

	private void deployHttpToHttps(DeploymentContext context) {
		if ( enabledHttpsAutoRedirect ) {
			log.info("Redireting HTTP requests to HTTPS");
			context.rootHandler(new AutoHTTPSRedirectHandler(context.rootHandler()));
		}
	}
}