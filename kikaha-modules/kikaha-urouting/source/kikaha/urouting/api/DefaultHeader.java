package kikaha.urouting.api;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Accessors(fluent = true)
public class DefaultHeader implements Header {

	final List<String> values = new ArrayList<>();

	@NonNull
	String name;

	public void add( String value ) {
		values.add( value );
	}
}
