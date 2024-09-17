package kikaha.rocker;

import java.util.*;
import io.undertow.util.HttpString;
import kikaha.core.cdi.helpers.TinyList;
import kikaha.urouting.api.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author <a href="mailto:j.milagroso@gmail.com">Jay Milagroso</a>
 */
@Getter
@Setter
@Accessors( fluent = true )
public class RockerResponse implements Response, MutableResponse {

    final RockerTemplate entity = new RockerTemplate();
    final List<Header> headers = new TinyList<>();
    final String contentType = Mimes.HTML;

    String encoding = "UTF-8";
    int statusCode = 200;

    public RockerResponse objects( final Object entity ) {
        this.entity.setObjects( entity );
        return this;
    }

    public RockerResponse templateName( final String templateName ) {
        this.entity.setTemplateName( templateName );
        return this;
    }

    @Override
    public RockerResponse entity(Object entity) {
        throw new UnsupportedOperationException("entity is immutable!");
    }

    @Override
    public RockerResponse headers(Iterable<Header> headers) {
        throw new UnsupportedOperationException("headers is immutable!");
    }

    @Override
    public RockerResponse header(HttpString name, String value) {
        throw new UnsupportedOperationException("header is immutable!");
    }
}