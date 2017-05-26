package kikaha.core.modules.security;

import javax.inject.*;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.CDI;
import kikaha.core.modules.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Singleton
@Getter
public class SecurityModule implements Module {

	final String name = "security";

	@Inject CDI provider;
	@Inject Config config;
	@Inject DefaultAuthenticationConfiguration defaultAuthenticationConfiguration;
	@Inject SecurityConfiguration securityConfiguration;

	@Override
	public void load(Undertow.Builder builder, final DeploymentContext context ) {
		final AuthenticationRuleMatcher ruleMatcher = createRuleMatcher();
		if ( !ruleMatcher.rules().isEmpty() ) {
			log.info( "Configuring authentication rules..." );
			configureAllAuthenticationMechanismsUsedOnTheApplication( ruleMatcher );
            defaultAuthenticationConfiguration.logDetailedInformationAboutThisConfig();
			securityConfiguration.logDetailedInformationAboutThisConfig();
			final HttpHandler rootHandler = context.rootHandler();
			final AuthenticationHttpHandler authenticationHandler = new AuthenticationHttpHandler(
					ruleMatcher, defaultAuthenticationConfiguration.getPermissionDeniedPage(),
					rootHandler, securityConfiguration );
			context.rootHandler(authenticationHandler);
		}
	}

	void configureAllAuthenticationMechanismsUsedOnTheApplication(AuthenticationRuleMatcher ruleMatcher) {
		final Collection<AuthenticationMechanism> configurable = getConfigurableMechanisms(ruleMatcher);
		configurable.forEach( m -> m.configure( securityConfiguration, defaultAuthenticationConfiguration ) );
	}

	Collection<AuthenticationMechanism> getConfigurableMechanisms( AuthenticationRuleMatcher ruleMatcher ){
		final Set<AuthenticationMechanism> configurable = new HashSet<>();
		ruleMatcher.rules().forEach( r -> configurable.addAll( r.mechanisms() ) );
        final ArrayList<AuthenticationMechanism> configurableSorted = new ArrayList<>(configurable);
        configurableSorted.sort((o1, o2) -> Integer.compare(o2.priority(), o1.priority()));
        return configurableSorted;
	}

	AuthenticationRuleMatcher createRuleMatcher() {
		return new AuthenticationRuleMatcher(
			provider, config.getConfig("server.auth"),
                defaultAuthenticationConfiguration);
	}
}