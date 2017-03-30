package kikaha.core.modules.security;

import javax.annotation.PostConstruct;
import javax.inject.*;
import kikaha.config.Config;
import lombok.*;

/**
 *
 */
@Getter
@Singleton
@ToString
public class FormAuthenticationConfiguration {

	@Inject Config config;

	private String loginPage;
	private String errorPage;
	private String successPage;
	private String callbackUrl;
	private String permissionDeniedPage;

	@PostConstruct
	public void readConfiguration(){
		final Config authConfig = this.config.getConfig( "server.auth.form-auth" );
		loginPage = authConfig.getString( "login-page" );
		errorPage = authConfig.getString( "error-page" );
		successPage = authConfig.getString( "success-page" );
		callbackUrl = authConfig.getString( "callback-url" );
		permissionDeniedPage = authConfig.getString( "permission-denied-page" );
	}
}
