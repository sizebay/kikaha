package kikaha.uworkers.local;

import java.io.IOException;
import java.util.concurrent.*;
import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.core.EndpointInboxSupplier;
import lombok.*;

@EqualsAndHashCode
@RequiredArgsConstructor
class LocalEndpointInboxSupplier implements EndpointInboxSupplier {

	final BlockingQueue<Exchange> messageQueue;

	@Override
	public Exchange receiveMessage() throws InterruptedException, IOException {
		return messageQueue.take();
	}

	void sendMessage( Exchange exchange ) {
		try {
			messageQueue.put( exchange );
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	static LocalEndpointInboxSupplier withFixedSize( int size ){
		return new LocalEndpointInboxSupplier( new ArrayBlockingQueue<>( size ) );
	}

	static LocalEndpointInboxSupplier withElasticSize(){
		return new LocalEndpointInboxSupplier( new LinkedBlockingQueue<>() );
	}
}