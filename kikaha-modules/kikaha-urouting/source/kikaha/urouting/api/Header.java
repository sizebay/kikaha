package kikaha.urouting.api;

import io.undertow.util.HttpString;

public interface Header {
	HttpString name();
	Iterable<String> values();
	void add( String value );
}
