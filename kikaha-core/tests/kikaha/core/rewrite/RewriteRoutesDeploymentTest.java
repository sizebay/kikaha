package kikaha.core.rewrite;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import io.undertow.server.handlers.proxy.ProxyHandler;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import kikaha.core.api.RequestHook;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.Provided;
import trip.spi.ServiceProvider;

@RunWith( MockitoJUnitRunner.class )
public class RewriteRoutesDeploymentTest {

	@Mock
	DeploymentContext context;

	@Provided( exposedAs = DeploymentHook.class )
	RewriteRoutesDeployment deployment;

	@Test
	public void ensureThatHaveDeployedFiveRewriteRoutesDefinedInConfFile()
	{
		deployment.onDeploy( context );
		verify( context, times( 5 ) ).register( any( RequestHook.class ) );
		verify( context ).rootHandler( any( ProxyHandler.class ) );
	}

	@Before
	@SneakyThrows
	public void provideDependencies()
	{
		val provider = new ServiceProvider();
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		provider.provideOn( this );
	}
}
