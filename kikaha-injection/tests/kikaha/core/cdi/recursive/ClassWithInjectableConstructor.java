package kikaha.core.cdi.recursive;

import javax.inject.Inject;

/**
 *
 */
public class ClassWithInjectableConstructor {

	final UserService userService;

	@Inject
	ClassWithInjectableConstructor(UserService userService ) {
		this.userService = userService;
	}
}
