package kikaha.core.modules.security;

import javax.annotation.PostConstruct;
import javax.inject.*;
import io.undertow.server.HttpServerExchange;
import kikaha.config.Config;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Getter
@Setter
@Singleton
@ToString
public class AuthenticationEndpoints {

	@Inject Config config;

	private String loginPage;
	private String loginTemplate;
	private String errorPage;
	private String successPage;
	private String callbackUrl;
	private String callbackUrlMethod;
	private String logoutUrl;
	private String logoutUrlMethod;
	private String permissionDeniedPage;

	@PostConstruct
	public void readConfiguration(){
		final Config authConfig = this.config.getConfig( "server.auth.endpoints" );
		loginTemplate = authConfig.getString( "login-template", "login.html" );
		loginPage = authConfig.getString( "login-page" );
		errorPage = authConfig.getString( "error-page" );
		successPage = authConfig.getString( "success-page" );
		callbackUrl = authConfig.getString( "callback-url" );
		callbackUrlMethod = config.getString( "callback-url-http-method", "POST" );
		logoutUrl = authConfig.getString( "logout-url" );
		logoutUrlMethod = authConfig.getString( "logout-url-http-method", "POST" );
		permissionDeniedPage = authConfig.getString( "permission-denied-page" );
	}

	public void logDetailedInformationAboutThisConfig() {
		log.info( "Defined authentication endpoints (depending on the modules you've loaded, not all endpoints are actually in use):" );
		log.info( "  login-page: " + loginPage );
		log.info( "  error-page: " + errorPage );
		log.info( "  success-page: " + successPage );
		log.info( "  callback-url: " + callbackUrl );
		log.info( "  logout-url: " + logoutUrl );
	}

	public boolean isTryingToLogin(HttpServerExchange exchange) {
		return getCallbackUrlMethod().equals( exchange.getRequestMethod().toString() )
				&& getCallbackUrl().equals( exchange.getRelativePath() );
	}
}
