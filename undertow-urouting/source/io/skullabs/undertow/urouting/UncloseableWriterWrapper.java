package io.skullabs.undertow.urouting;

import java.io.Closeable;
import java.io.Writer;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( staticName="wrap" )
public class UncloseableWriterWrapper extends Writer {

	@Delegate( excludes=Closeable.class )
	final Writer writer;

	public void close() {
	}
}
