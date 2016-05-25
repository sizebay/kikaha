package kikaha.core.modules.smart;

import java.util.Set;
import kikaha.core.url.URLMatcher;
import lombok.AllArgsConstructor;

/**
 *
 */
@AllArgsConstructor
public class CORSConfig {

	boolean alwaysAllowOrigin;
	Set<String> allowedMethods;
	Set<URLMatcher> allowedOrigins;

	public String toString(){
		return "alwaysAllowOrigin: " + alwaysAllowOrigin + "; "
			+  "allowedMethods: " + allowedMethods + "; "
			+  "allowedOrigins: " + allowedOrigins;
	}
}
