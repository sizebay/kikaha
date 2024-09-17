package kikaha.hazelcast;

import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.MonolithSessionStore;
import lombok.Getter;

import javax.inject.Inject;

/**
 * Created by miere.teixeira on 27/07/2017.
 */
public abstract class HazelcastMonolithSessionStore extends MonolithSessionStore {

    @Getter
    @Inject
    HazelcastSessionStore sessionStore;
}