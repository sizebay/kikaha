package kikaha.urouting.api;

import java.util.List;
import io.undertow.util.HttpString;
import kikaha.core.cdi.helpers.TinyList;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public class DefaultHeader implements Header {

	final HttpString name;
	final List<String> values;

	public void add( String value ) {
		values.add( value );
	}

	public static Header createHeader( HttpString name, String value ) {
		return new DefaultHeader( name, new TinyList<>( value ) );
	}
}
