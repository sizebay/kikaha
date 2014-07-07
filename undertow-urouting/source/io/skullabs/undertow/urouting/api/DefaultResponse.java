package io.skullabs.undertow.urouting.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors( fluent=true )
@NoArgsConstructor
@RequiredArgsConstructor
public class DefaultResponse implements Response {

	@NonNull Object entity;
	@NonNull Integer statusCode = 200;
	@NonNull String encoding = "UTF-8";
	@NonNull String contentType = Mimes.PLAIN_TEXT;
	@NonNull List<Header> headers = new ArrayList<>();

	public DefaultResponse header( String name, String value ) {
		Header header = getHeader( name );
		if ( header == null ) {
			header = new DefaultHeader( name );
			headers.addAll(headers);
		}
		header.add(value);
		return this;
	}

	protected Header getHeader( String name ) {
		for ( Header header : headers )
			if ( header.name().equals(name) )
				return header;
		return null;
	}
	
	private static DefaultResponse response() {
		return new DefaultResponse();
	}

	public static DefaultResponse ok() {
		return response().statusCode(200);
	}

	public static DefaultResponse ok( Object entity ) {
		return ok().entity(entity);
	}

	public static DefaultResponse noContent() {
		return response().statusCode(204);
	}

	public static DefaultResponse notModified() {
		return response().statusCode(304);
	}

	public static DefaultResponse seeOther() {
		return response().statusCode(303);
	}

	public static DefaultResponse seeOther( String location ) {
		return response().statusCode(303)
				.header("Location", location);
	}

	public static DefaultResponse serverError() {
		return response().statusCode(500);
	}

	public static DefaultResponse serverError( String string ) {
		return response().statusCode(500)
				.entity(string);
	}

	public static DefaultResponse temporaryRedirect() {
		return response().statusCode(307);
	}

	public static DefaultResponse temporaryRedirect( String location ) {
		return response().statusCode(307)
				.header("Location", location);
	}

	public static DefaultResponse temporaryRedirect( URI location ) {
		return response().statusCode(307)
				.header("Location", location.toString());
	}
}