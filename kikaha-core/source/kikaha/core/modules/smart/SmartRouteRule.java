package kikaha.core.modules.smart;

import kikaha.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class SmartRouteRule {

	final String virtualHost;
	final String path;
	final String target;

	public static SmartRouteRule from(Config config ) {
		return new SmartRouteRule(
			config.getString("virtual-host", "{virtualHost}"),
			config.getString("path", "/{path}"),
			config.getString("to")
		);
	}

	@Override
	public String toString() {
		return "virtual-host(" + virtualHost + ") " +
				"AND path(" + path + ") " +
				"-> " + target;
	}
}