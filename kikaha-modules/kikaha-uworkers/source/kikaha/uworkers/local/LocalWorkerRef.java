package kikaha.uworkers.local;

import kikaha.uworkers.api.*;
import lombok.*;

/**
 *
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class LocalWorkerRef implements WorkerRef {

	final LocalEndpointInboxSupplier supplier;

	@Override
	public <REQ> Response send( REQ request) {
		final LocalExchange exchange = LocalExchange.of( request );
		return send( exchange );
	}

	@Override
	public Response send( Exchange exchange ) {
		supplier.sendMessage( exchange );
		return exchange;
	}
}
