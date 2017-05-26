package kikaha.core.modules.security;

import javax.annotation.PostConstruct;
import javax.inject.*;
import kikaha.config.Config;
import kikaha.core.cdi.CDI;
import lombok.*;

/**
 * Represents all user defined configuration that should be available on the application.
 */
@Getter
@Setter( AccessLevel.PACKAGE )
@Singleton
public class SecurityConfiguration {

	@Inject private Config config;
	@Inject private CDI provider;

	private SecurityContextFactory factory;
	private SessionIdManager sessionIdManager;
	private SessionStore sessionStore;
	private PasswordEncoder passwordEncoder;
	private AuthenticationRequestMatcher authenticationRequestMatcher;
	private AuthenticationSuccessListener authenticationSuccessListener;
	private AuthenticationFailureListener authenticationFailureListener;

	@PostConstruct
	public void readConfiguration(){
		factory = loadConfiguredClass( "server.auth.security-context-factory", SecurityContextFactory.class );
		sessionIdManager = loadConfiguredClass( "server.auth.session-id-manager", SessionIdManager.class );
		sessionStore = loadConfiguredClass( "server.auth.session-store", SessionStore.class );
		passwordEncoder = loadConfiguredClass( "server.auth.password-encoder", PasswordEncoder.class );
		authenticationRequestMatcher = loadConfiguredClass( "server.auth.authentication-request-matcher", AuthenticationRequestMatcher.class );
		authenticationSuccessListener = loadConfiguredClass( "server.auth.authentication-success-listener", AuthenticationSuccessListener.class );
		authenticationFailureListener = loadConfiguredClass( "server.auth.authentication-failure-listener", AuthenticationFailureListener.class );
	}

	@SuppressWarnings( "all" )
	public <T> T loadConfiguredClass( String path, Class<T> expectedType ) {
		expectedType = (Class<T>)config.getClass( path );
		return provider.load( expectedType );
	}

	/**
	 * Defines a new {@link AuthenticationRequestMatcher} if not previously defined.
	 * @param authenticationRequestMatcher
	 * @return
	 */
	public SecurityConfiguration setRequestMatcherIfAbsent( AuthenticationRequestMatcher authenticationRequestMatcher ) {
		if ( this.authenticationRequestMatcher == null )
			this.authenticationRequestMatcher = authenticationRequestMatcher;
		return this;
	}

	/**
	 * Defines a new {@link AuthenticationSuccessListener} if not previously defined.
	 *
	 * @param authenticationSuccessListener
	 * @return
	 */
	public SecurityConfiguration setSuccessListenerIfAbsent( AuthenticationSuccessListener authenticationSuccessListener ) {
		if ( this.authenticationSuccessListener == null )
			this.authenticationSuccessListener = authenticationSuccessListener;
		return this;
	}

	/**
	 * Defines a new {@link AuthenticationFailureListener} if not previously defined.
	 *
	 * @param authenticationFailureListener
	 * @return
	 */
	public SecurityConfiguration setFailureListenerIfAbsent( AuthenticationFailureListener authenticationFailureListener ) {
		if ( this.authenticationFailureListener == null )
			this.authenticationFailureListener = authenticationFailureListener;
		return this;
	}
}
