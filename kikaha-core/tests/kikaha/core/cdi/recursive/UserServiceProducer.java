package kikaha.core.cdi.recursive;

import kikaha.core.cdi.Provided;
import kikaha.core.cdi.ProducerFactory;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.cdi.ServiceProviderException;

public class UserServiceProducer implements ProducerFactory<UserService> {

	@Provided
	UserService userService;

	@Override
	public UserService provide( ProviderContext context ) throws ServiceProviderException {
		return userService;
	}
}
