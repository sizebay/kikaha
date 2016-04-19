package kikaha.urouting;

import java.io.Closeable;
import java.io.OutputStream;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor( staticName="wrap" )
public class UncloseableWriterWrapper extends OutputStream {

	@Delegate( excludes=Closeable.class )
	final OutputStream outputStream;

	@Override
	public void close() {
	}
}
