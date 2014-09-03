package kikaha.core.impl.conf;

import kikaha.core.api.conf.FormAuthConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class DefaultFormAuthConfiguration implements FormAuthConfiguration {

	final Config config;

	@Getter( lazy = true )
	private final String name = config().getString( "name" );

	@Getter( lazy = true )
	private final String loginPage = config().getString( "login-page" );

	@Getter( lazy = true )
	private final String errorPage = config().getString( "error-page" );

	@Getter( lazy = true )
	private final String postLocation = config().getString( "post-location" );

	@Getter( lazy = true )
	private final String permitionDeniedPage =
		config().getString( "permition-denied-page" );
}
