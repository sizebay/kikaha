package kikaha.core.cdi.recursive;

import javax.inject.Inject;

public class UserService {

	final User user = new User();

	@Inject
	ProfileService profileService;
}
