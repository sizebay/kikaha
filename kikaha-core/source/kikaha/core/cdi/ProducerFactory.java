package kikaha.core.cdi;

public interface ProducerFactory<T> {

	T provide( ProviderContext context ) throws ServiceProviderException;

}
