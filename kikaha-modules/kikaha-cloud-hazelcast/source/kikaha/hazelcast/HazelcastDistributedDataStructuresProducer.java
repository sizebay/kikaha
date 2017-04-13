package kikaha.hazelcast;

import com.hazelcast.core.*;
import kikaha.core.cdi.ProviderContext;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@SuppressWarnings( "rawtypes" )
public class HazelcastDistributedDataStructuresProducer {

	@Inject
	HazelcastInstance hazelcast;

	@Inject
	HazelcastProducedDataListenerFactory listenerFactory;

	@Produces
	public IAtomicLong produceIAtomicLongs( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IAtomicLong data = hazelcast.getAtomicLong( name );
		notifyDataWasProduced( data, IAtomicLong.class );
		return data;
	}

	@Produces
	public IAtomicReference produceIAtomicReferences( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IAtomicReference data = hazelcast.getAtomicReference( name );
		notifyDataWasProduced( data, IAtomicReference.class );
		return data;
	}

	@Produces
	public IMap produceIMaps( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IMap data = hazelcast.getMap( name );
		notifyDataWasProduced( data, IMap.class );
		return data;
	}

	@Produces
	public IQueue produceIQueues( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IQueue queue = hazelcast.getQueue( name );
		notifyDataWasProduced(queue, IQueue.class);
		return queue;
	}

	@Produces
	public MultiMap produceMultimap( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final MultiMap mmap = hazelcast.getMultiMap( name );
		notifyDataWasProduced(mmap, MultiMap.class);
		return mmap;
	}

	@Produces
	public ISet produceSet( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final ISet set = hazelcast.getSet( name );
		notifyDataWasProduced(set, ISet.class);
		return set;
	}

	@Produces
	public IList produceList( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IList list = hazelcast.getList( name );
		notifyDataWasProduced(list, IList.class);
		return list;
	}

	@Produces
	public ITopic produceTopic( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final ITopic topic = hazelcast.getTopic( name );
		notifyDataWasProduced(topic, ITopic.class);
		return topic;
	}

	@Produces
	public ILock produceLock( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final ILock lock = hazelcast.getLock( name );
		notifyDataWasProduced(lock, ILock.class);
		return lock;
	}

	@Produces
	public IExecutorService produceIExecutorService(final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IExecutorService executorService = hazelcast.getExecutorService(name);
		notifyDataWasProduced(executorService, IExecutorService.class);
		return executorService;
	}

	@Produces
	public ReplicatedMap produceReplicatedMap( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final ReplicatedMap data = hazelcast.getReplicatedMap(name);
		notifyDataWasProduced(data, ReplicatedMap.class);
		return data;
	}

	@Produces
	public IdGenerator produceIdGenerator(final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IdGenerator data = hazelcast.getIdGenerator( name );
		notifyDataWasProduced( data, IdGenerator.class );
		return data;
	}

	private String retrieveSourceNameFrom( final ProviderContext context ) {
		final Named name = context.getAnnotation( Named.class );
		if ( name != null )
			return name.value();
		throw new IllegalStateException( "Can't produce data: no name provided" );
	}

	@SuppressWarnings("unchecked")
	private <T extends DistributedObject> void notifyDataWasProduced(T data, Class<?> dataType ) {
		final List<HazelcastProducedDataListener<?>> listeners = listenerFactory.getListenerFor( dataType );
		for ( final HazelcastProducedDataListener listener : listeners ) {
			if ( listener != null )
				listener.dataProduced( data );
		}
	}
}
