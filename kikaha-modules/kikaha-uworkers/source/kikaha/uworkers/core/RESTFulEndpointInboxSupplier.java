package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An implementation of {@link EndpointInboxSupplier} that allow the REST API to
 * populate poll tasks to be consumed by the {@link WorkerEndpointMessageListener}s.
 */
@RequiredArgsConstructor
public class RESTFulEndpointInboxSupplier implements EndpointInboxSupplier {

    final BlockingQueue<Exchange> taskQueue;
    final EndpointInboxSupplier supplier;

    @Override
    public Exchange receiveMessage() throws InterruptedException, IOException, EndpointInboxConsumerTimeoutException {
        Exchange exchange = taskQueue.poll();
        if ( exchange == null )
            exchange = supplier.receiveMessage();
        return exchange;
    }

    public boolean poll( Exchange exchange ) {
        return taskQueue.offer( exchange );
    }

    public static RESTFulEndpointInboxSupplier wrap( final EndpointInboxSupplier supplier, final int maxTaskQueueSize ){
        final BlockingQueue<Exchange> taskQueue = maxTaskQueueSize > 0
                ? new ArrayBlockingQueue<>( maxTaskQueueSize )
                : new LinkedBlockingQueue<>();
        return new RESTFulEndpointInboxSupplier( taskQueue, supplier );
    }
}
