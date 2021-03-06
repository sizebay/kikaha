package kikaha.mustache;

import java.util.Collections;
import java.util.Iterator;

import kikaha.urouting.api.Header;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors( fluent = true )
@NoArgsConstructor( staticName = "ok" )
public class MustacheResponse implements Response {

	final MustacheTemplate entity = new MustacheTemplate();
	final String encoding = "UTF-8";
	final Iterable<Header> headers = EmptyHeaders.INSTANCE;

	@NonNull
	String contentType = Mimes.HTML;

	@NonNull
	Integer statusCode = 200;

	public MustacheResponse paramObject( final Object entity ) {
		this.entity.paramObject( entity );
		return this;
	}

	public MustacheResponse templateName( final String templateName ) {
		this.entity.templateName( templateName );
		return this;
	}
}

class EmptyHeaders implements Iterable<Header> {

	static EmptyHeaders INSTANCE = new EmptyHeaders();
	final Iterator<Header> iterator = Collections.emptyIterator();

	@Override
	public Iterator<Header> iterator() {
		return iterator;
	}
}