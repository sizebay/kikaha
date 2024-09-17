package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.api.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * Represents a failure while retrieving an {@link Exchange} from
 * the {@link EndpointInboxSupplier}.
 */
@Slf4j
@RequiredArgsConstructor
public class FailureExchange implements Exchange {

    final Throwable failure;

    @Override
    public <RESP> RESP response() {
        throw failure();
    }

    @Override
    public <RESP> RESP responseAs(Class<RESP> targetClass) {
        throw failure();
    }

    @Override
    public Response then(BiConsumer<UndefinedObject, Throwable> listener) {
        throw failure();
    }

    @Override
    public <REQ> REQ request() {
        throw failure();
    }

    @Override
    public <REQ> REQ requestAs(Class<REQ> targetClass) throws IOException {
        throw failure();
    }

    @Override
    public <RESP> Exchange reply(RESP response) {
        throw failure();
    }

    @Override
    public Exchange reply(Throwable error) {
        throw failure();
    }

    private RuntimeException failure(){
        return new IllegalStateException( failure );
    }
}
