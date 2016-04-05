package kikaha.core.cdi.recursive;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProfileService {

	@Inject
	UserCategoryService userCategoryService;
}
