package io.skullabs.undertow.urouting;

import java.io.Closeable;
import java.io.Writer;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor( staticName="wrap" )
public class UncloseableWriterWrapper extends Writer {

	@Delegate( excludes=Closeable.class )
	final Writer writer;

	public void close() {
	}
}
