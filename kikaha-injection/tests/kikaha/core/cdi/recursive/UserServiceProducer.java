package kikaha.core.cdi.recursive;

import javax.inject.Inject;

import kikaha.core.cdi.ProducerFactory;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.cdi.ServiceProviderException;

public class UserServiceProducer implements ProducerFactory<UserService> {

	@Inject
	UserService userService;

	@Override
	public UserService provide( ProviderContext context ) throws ServiceProviderException {
		return userService;
	}
}
