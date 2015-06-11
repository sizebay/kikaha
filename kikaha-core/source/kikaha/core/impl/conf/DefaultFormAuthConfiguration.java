package kikaha.core.impl.conf;

import kikaha.core.api.conf.FormAuthConfiguration;
import lombok.Getter;
import lombok.experimental.Accessors;

import com.typesafe.config.Config;

@Getter
@Accessors( fluent = true )
public class DefaultFormAuthConfiguration implements FormAuthConfiguration {

	final String name;
	final String loginPage;
	final String errorPage;
	final String postLocation;
	final String permitionDeniedPage;

	public DefaultFormAuthConfiguration( final Config config ) {
		this.name = config.getString( "name" );
		this.loginPage = config.getString( "login-page" );
		this.errorPage = config.getString( "error-page" );
		this.postLocation = config.getString( "post-location" );
		this.permitionDeniedPage = config.getString( "permition-denied-page" );
	}
}
