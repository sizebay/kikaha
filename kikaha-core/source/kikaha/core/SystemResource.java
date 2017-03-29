package kikaha.core;

import java.io.*;

/**
 *
 */
public interface SystemResource {

	static String readFileAsString( String fileName, String encoding) {
		try ( InputStream file = openFile( fileName ) ) {
			return readFileAsString( file, encoding );
		} catch ( IOException cause ) {
			throw new IllegalStateException( cause );
		}
	}

	static String readFileAsString(InputStream inputStream, String encoding) {
		try {
			return readFile(inputStream).toString(encoding);
		} catch ( UnsupportedEncodingException cause ) {
			throw new IllegalStateException( cause );
		}
	}

	static byte[] readFileAsBytes(InputStream inputStream) {
		return readFile(inputStream).toByteArray();
	}

	static ByteArrayOutputStream readFile( final InputStream inputStream ) {
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final byte[] buffer = new byte[1024];
			int length;
			while ( ( length = inputStream.read( buffer ) ) != -1 )
				baos.write( buffer, 0, length );
			return baos;
		} catch ( IOException cause ) {
			throw new IllegalStateException( cause );
		}
	}

	static InputStream openFile( String fileName ){
		return open( new File( fileName ) );
	}

	static InputStream open( File file ) {
		try {
			if ( file.exists() )
				return new FileInputStream( file );
			final String fileName = file.getName().replaceFirst( "^/", "" );
			return ClassLoader.getSystemResourceAsStream( fileName );
		} catch ( IOException cause ) {
			throw new IllegalStateException( cause );
		}
	}
}
