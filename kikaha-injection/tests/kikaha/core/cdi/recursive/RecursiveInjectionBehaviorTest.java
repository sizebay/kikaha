package kikaha.core.cdi.recursive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import javax.inject.Inject;

import kikaha.core.cdi.DefaultServiceProvider;

import org.junit.Before;
import org.junit.Test;

public class RecursiveInjectionBehaviorTest {

	@Inject
	ProfileService profileService;

	@Inject
	UserCategoryService userCategoryService;

	@Inject
	UserService userService;

	@Inject
	User user;

	@Test( timeout = 1000l )
	public void ensureThatCanInjectServicesThatAreMutuallyDependent() {
		assertNotNull( profileService );
		assertNotNull( userCategoryService );
		assertNotNull( userService );
	}

	@Test( timeout = 1000l )
	public void ensureThatServicesHaveItsDependenciesResolved() {
		assertSame( profileService.userCategoryService, userCategoryService );
		assertSame( userService.profileService, profileService );
		assertSame( userCategoryService.userService, userService );
	}

	@Test( timeout = 1000l )
	public void ensureThatInstancesProducedWithRecursiveDependenciesAreNotNull() {
		assertNotNull( user );
	}

	@Before
	public void provideDependencies() {
		new DefaultServiceProvider().provideOn( this );
	}
}
