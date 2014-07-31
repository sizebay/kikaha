package io.skullabs.undertow.urouting.api;

public interface Header {
	String name();
	Iterable<String> values();
	void add( String value );
}
