package kikaha.urouting.api;

import static io.undertow.util.Headers.LOCATION;
import java.net.URI;
import java.util.*;
import io.undertow.util.*;
import kikaha.core.cdi.helpers.TinyList;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors( fluent=true )
@NoArgsConstructor
public class DefaultResponse implements Response {

	@NonNull Object entity = "";
	@NonNull Integer statusCode = 200;
	@NonNull String encoding = "UTF-8";
	@NonNull String contentType = Mimes.PLAIN_TEXT;
	@NonNull List<Header> headers = new TinyList<>();

	public DefaultResponse header( final String name, final String value ) {
		return header( new HttpString(name), value );
	}

	public DefaultResponse header(final HttpString name, final String value ) {
		Header header = getHeader( name );
		if ( header == null ) {
			header = DefaultHeader.createHeader( name, value );
			headers.add(header);
		} else
			header.add(value);
		return this;
	}

	/**
	 * Good-enough method to retrieve headers.
	 *
	 * @param name
	 * @return
	 */
	protected Header getHeader( final HttpString name ) {
		for ( final Header header : headers )
			if ( header.name().equals(name) )
				return header;
		return null;
	}

	public static DefaultResponse response() {
		return new DefaultResponse();
	}

	public static DefaultResponse response( final int statusCode ) {
		return new DefaultResponse().statusCode(statusCode);
	}

	public static DefaultResponse ok() {
		return response().statusCode(200);
	}

	public static DefaultResponse ok( final Object entity ) {
		return ok().entity(entity);
	}

	public static DefaultResponse noContent() {
		return response().statusCode(204);
	}

	public static DefaultResponse created() {
		return response().statusCode(201);
	}

	public static DefaultResponse created( final String location ) {
		return response().statusCode(201)
				.header(LOCATION, location);
	}

	public static DefaultResponse accepted() {
		return response().statusCode(202);
	}

	public static DefaultResponse movedPermanently() {
		return response().statusCode(301);
	}

	public static DefaultResponse notModified() {
		return response().statusCode(304);
	}

	public static DefaultResponse seeOther() {
		return response().statusCode(303);
	}

	public static DefaultResponse seeOther( final String location ) {
		return response().statusCode(303)
				.header(LOCATION, location);
	}

	public static DefaultResponse temporaryRedirect( final String location ) {
		return response().statusCode(307)
				.header(LOCATION, location);
	}

	public static DefaultResponse temporaryRedirect( final URI location ) {
		return temporaryRedirect( location.toString() );
	}

	public static DefaultResponse permanentRedirect( final String location ) {
		return response().statusCode(308)
				.header(LOCATION, location);
	}

	public static DefaultResponse permanentRedirect( final URI location ) {
		return permanentRedirect( location.toString() );
	}

	public static DefaultResponse badRequest() {
		return response().statusCode(400);
	}

	public static DefaultResponse unauthorized() {
		return response().statusCode(401);
	}

	public static DefaultResponse forbiden() {
		return response().statusCode(403);
	}

	public static DefaultResponse notFound() {
		return response().statusCode(404);
	}

	public static DefaultResponse preconditionFailed() {
		return response().statusCode(412);
	}

	public static DefaultResponse serverError() {
		return response().statusCode(500);
	}

	public static DefaultResponse serverError( final String string ) {
		return response().statusCode(500)
				.entity(string);
	}
}