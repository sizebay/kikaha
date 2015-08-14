package kikaha.core.rewrite;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import io.undertow.server.handlers.proxy.ProxyHandler;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentListener;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;

@RunWith( MockitoJUnitRunner.class )
public class RewriteRoutesDeploymentTest {

	@Mock
	DeploymentContext context;

	@Provided( exposedAs = DeploymentListener.class )
	RewriteRoutesDeployment deployment;

	@Test
	public void ensureThatHaveDeployedFiveRewriteRoutesDefinedInConfFile()
	{
		deployment.onDeploy( context );
		verify( context, times( 6 ) ).rootHandler( any( ProxyHandler.class ) );
	}

	@Before
	@SneakyThrows
	public void provideDependencies()
	{
		val provider = new DefaultServiceProvider();
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		provider.provideOn( this );
	}
}
