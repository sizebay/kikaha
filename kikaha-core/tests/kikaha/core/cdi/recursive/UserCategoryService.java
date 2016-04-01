package kikaha.core.cdi.recursive;

import kikaha.core.cdi.Provided;
import kikaha.core.cdi.Singleton;

@Singleton
public class UserCategoryService {

	@Provided
	UserService userService;
}
