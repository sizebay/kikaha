package kikaha.core.cdi.recursive;

import kikaha.core.cdi.Provided;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.cdi.ServiceProviderException;
import kikaha.core.cdi.ProducerFactory;

public class UserProducer implements ProducerFactory<User> {

	@Provided
	UserService userService;

	@Override
	public User provide( ProviderContext context ) throws ServiceProviderException {
		return userService.user;
	}
}
