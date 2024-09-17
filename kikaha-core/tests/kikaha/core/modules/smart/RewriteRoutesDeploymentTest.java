package kikaha.core.modules.smart;

import io.undertow.server.handlers.proxy.ProxyHandler;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.DefaultCDI;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith( MockitoJUnitRunner.class )
public class RewriteRoutesDeploymentTest {

	@Mock
	DeploymentContext context;

	@Inject
	RewriteRoutesModule deployment;

	@Test
	public void ensureThatHaveDeployedFiveRewriteRoutesDefinedInConfFile()
	{
		deployment.load( null, context );
		verify( context, times( 5 ) ).rootHandler( any( ProxyHandler.class ) );
	}

	@Before
	@SneakyThrows
	public void provideDependencies()
	{
		new DefaultCDI().injectOn( this );
	}
}
