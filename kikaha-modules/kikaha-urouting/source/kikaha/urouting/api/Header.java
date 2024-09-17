package kikaha.urouting.api;

import java.util.List;
import io.undertow.util.HttpString;

public interface Header {
	HttpString name();
	List<String> values();
	void add( String value );
}
