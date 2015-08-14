package kikaha.core.api.conf;

import java.util.List;

public interface Routes {
	
	String responseEncoding();

	String requestEncoding();

	List<RewritableRule> rewriteRoutes();

	List<RewritableRule> reverseProxyRoutes();
}
