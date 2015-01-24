package kikaha.core.impl.conf;

import kikaha.core.api.conf.RewriteRoute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@NoArgsConstructor
@AllArgsConstructor
public class DefaultRewriteRoute implements RewriteRoute {

	String virtualHost;
	String path;
	String target;
}
