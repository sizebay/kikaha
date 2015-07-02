package kikaha.hazelcast;

import kikaha.hazelcast.config.HazelcastProducedDataListener;
import kikaha.hazelcast.config.HazelcastProducedDataListenerFactory;
import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.ProviderContext;
import trip.spi.Singleton;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MultiMap;

@Singleton
public class HazelcastDistributedDataStructuresProducer {

	@Provided
	HazelcastInstance hazelcast;
	
	@Provided
	HazelcastProducedDataListenerFactory listenerFactory;

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IMap produceIMaps( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IMap map = hazelcast.getMap( name );
		notifyDataWasProduced(map, IMap.class);
		return map;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IQueue produceIQueues( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IQueue queue = hazelcast.getQueue( name );
		notifyDataWasProduced(queue, IQueue.class);
		return queue;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public MultiMap produceMultimap( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final MultiMap mmap = hazelcast.getMultiMap( name );
		notifyDataWasProduced(mmap, MultiMap.class);
		return mmap;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public ISet produceSet( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final ISet set = hazelcast.getSet( name );
		notifyDataWasProduced(set, ISet.class);
		return set;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IList produceList( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IList list = hazelcast.getList( name );
		notifyDataWasProduced(list, IList.class);
		return list;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public ITopic produceTopic( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final ITopic topic = hazelcast.getTopic( name );
		notifyDataWasProduced(topic, ITopic.class);
		return topic;
	}

	@Producer
	public ILock produceLock( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final ILock lock = hazelcast.getLock( name );
		notifyDataWasProduced(lock, ILock.class);
		return lock;
	}

	@Producer
	public IExecutorService produceIExecutorService( final ProviderContext context ) {
		final String name = retrieveSourceNameFrom( context );
		final IExecutorService executorService = hazelcast.getExecutorService(name);
		notifyDataWasProduced(executorService, IExecutorService.class);
		return executorService;
	}

	String retrieveSourceNameFrom( final ProviderContext context ) {
		final Source name = context.getAnnotation( Source.class );
		if ( name != null )
			return name.value();
		throw new IllegalStateException( "Can't produce data: no name provided" );
	}

	@SuppressWarnings("unchecked")
	<T extends DistributedObject> void notifyDataWasProduced( T data, Class<?> dataType ){
		HazelcastProducedDataListener<T> listener = (HazelcastProducedDataListener<T>)
				listenerFactory.getListenerFor( dataType );
		if ( listener != null )
			listener.dataProduced(data);
	}
}
