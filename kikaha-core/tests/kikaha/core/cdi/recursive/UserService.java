package kikaha.core.cdi.recursive;

import kikaha.core.cdi.Provided;

public class UserService {

	final User user = new User();

	@Provided
	ProfileService profileService;
}
