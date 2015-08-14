package kikaha.core.api;

import io.undertow.Undertow.Builder;

public interface UndertowServerConfiguration {

	void configure( Builder builder );
}
