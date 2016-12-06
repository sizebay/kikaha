/**
 *
 */
package kikaha.mojo.packager;

import java.io.*;

interface packager {

	String
		MESSAGE_CANT_ADD_TO_ZIP = "Can't add file",
		MESSAGE_CANT_CREATE_ZIP = "Can't create zip file";

	static void copy(final InputStream from, Writable to) throws IOException {
		final byte[] bytes = new byte[1024];
		int length;
		while ( ( length = from.read( bytes ) ) >= 0 )
			to.write( bytes, 0, length );
	}
}

interface FileMerger {
	void add(InputStream inputStream) throws IOException;
	String merge() throws IOException;
	String getFileName();
}

interface Writable {
	void write(byte[] b, int off, int len) throws IOException;
}