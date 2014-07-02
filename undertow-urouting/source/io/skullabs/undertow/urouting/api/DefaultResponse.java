package io.skullabs.undertow.urouting.api;

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
}
