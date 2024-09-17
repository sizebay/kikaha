package kikaha.uworkers.core;

import kikaha.uworkers.api.Exchange;
import kikaha.uworkers.api.Response;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * Empty implementation of {@link Exchange}.
 */
public class EmptyExchange implements Exchange {

    @Override
    public <RESP> RESP response() {
        return null;
    }

    @Override
    public <RESP> RESP responseAs(Class<RESP> targetClass) {
        return null;
    }

    @Override
    public Response then(BiConsumer<UndefinedObject, Throwable> listener) {
        return null;
    }

    @Override
    public <REQ> REQ request() {
        return null;
    }

    @Override
    public <REQ> REQ requestAs(Class<REQ> targetClass) throws IOException {
        return null;
    }

    @Override
    public <RESP> Exchange reply(RESP response) {
        return null;
    }

    @Override
    public Exchange reply(Throwable error) {
        return null;
    }
}
