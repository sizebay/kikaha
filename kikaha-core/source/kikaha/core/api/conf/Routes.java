package kikaha.core.api.conf;

import java.util.List;

public interface Routes {

	List<RewritableRule> rewriteRoutes();

	List<RewritableRule> reverseProxyRoutes();
}
