package kikaha.core.modules.smart;

import io.undertow.server.HttpServerExchange;

import java.util.Map;
import java.util.function.BiFunction;

public interface RequestMatcher extends BiFunction<HttpServerExchange, Map<String, String>, Boolean> {

}
