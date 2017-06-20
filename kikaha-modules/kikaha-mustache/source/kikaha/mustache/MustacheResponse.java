package kikaha.mustache;

import io.undertow.util.HttpString;
import kikaha.core.cdi.helpers.TinyList;
import kikaha.urouting.api.*;
import lombok.*;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors( fluent = true )
@NoArgsConstructor( staticName = "ok" )
public class MustacheResponse implements Response, MutableResponse {

	final MustacheTemplate entity = new MustacheTemplate();
	final Iterable<Header> headers = new TinyList<>();
	final String contentType = Mimes.HTML;

	String encoding = "UTF-8";
	int statusCode = 200;

	public MustacheResponse paramObject( final Object entity ) {
		this.entity.paramObject( entity );
		return this;
	}

	public MustacheResponse templateName( final String templateName ) {
		this.entity.templateName( templateName );
		return this;
	}

	@Override
	public MustacheResponse entity(Object entity) {
		throw new UnsupportedOperationException("entity is immutable!");
	}

	@Override
	public MustacheResponse headers(Iterable<Header> headers) {
		throw new UnsupportedOperationException("headers is immutable!");
	}

	@Override
	public MustacheResponse header(HttpString name, String value) {
		throw new UnsupportedOperationException("header is immutable!");
	}
}