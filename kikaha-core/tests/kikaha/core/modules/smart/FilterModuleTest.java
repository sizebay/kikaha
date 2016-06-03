package kikaha.core.modules.smart;

import static java.util.Collections.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.*;
import javax.inject.Inject;
import io.undertow.server.HttpHandler;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.helpers.TinyList;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 * Unit tests for {@link FilterModule}.
 */
@RunWith(KikahaRunner.class)
public class FilterModuleTest {

	@Mock Filter singleFilter;
	@Mock HttpHandler rootHandler;
	@Mock DeploymentContext deploymentContext;

	@Inject FilterModule injectedFilterModule;
	@Inject EmptyFilter emptyFilter;

	@Before
	public void configureDeploymentContext(){
		MockitoAnnotations.initMocks( this );
		doReturn( rootHandler ).when( deploymentContext ).rootHandler();
	}

	@Test
	public void ensureThatInjectedModuleCouldFoundTheEmptyFilterInTheClassPath(){
		assertEquals( 2, injectedFilterModule.foundFilters.size() );
		final Filter filter = asList(injectedFilterModule.foundFilters).get(0);
		assertEquals( emptyFilter, filter );
	}

	@Test
	public void ensureThatInjectedModuleCouldFoundTheRedirectionFilterDefinedAtTheConfigurationFile(){
		assertEquals( 2, injectedFilterModule.foundFilters.size() );
		final Filter filter = asList(injectedFilterModule.foundFilters).get(1);
		assertEquals( RedirectionFilter.class, filter.getClass() );
	}

	@Test
	public void ensureThatDeploysTheFilterHttpHandlerWhenHasFilters() throws IOException {
		final FilterModule filterModule = new FilterModule();
		filterModule.foundFilters = singletonList(singleFilter);
		filterModule.load( null, deploymentContext );
		verify(deploymentContext).rootHandler( any( FilterHttpHandler.class ) );
	}

	@Test
	public void ensureThatDoesNotDeployTheFilterHttpHandlerWhenHasNoFilters() throws IOException {
		final FilterModule filterModule = new FilterModule();
		filterModule.foundFilters = emptyList();
		filterModule.load( null, deploymentContext );
		verify( deploymentContext, never() ).rootHandler( any( FilterHttpHandler.class ) );
	}

	private static <T> List<T> asList( Collection<T> c ){
		final TinyList<T> newList = new TinyList<>();
		newList.addAll( c );
		return newList;
	}
}
