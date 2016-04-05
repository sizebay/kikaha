package kikaha.core.cdi.recursive;

import javax.inject.Inject;

import kikaha.core.cdi.ProducerFactory;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.cdi.ServiceProviderException;

public class UserProducer implements ProducerFactory<User> {

	@Inject
	UserService userService;

	@Override
	public User provide( ProviderContext context ) throws ServiceProviderException {
		return userService.user;
	}
}
