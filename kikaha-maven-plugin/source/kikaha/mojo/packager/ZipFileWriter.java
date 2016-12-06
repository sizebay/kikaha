package kikaha.mojo.packager;

import static kikaha.mojo.packager.packager.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import lombok.Getter;
import org.apache.maven.plugin.MojoExecutionException;

@Getter
public class ZipFileWriter {

	final List<String> prefixesToStripOutFromName = new ArrayList<>();
	final ZipOutputStream output;
	final String fileName;
	final String rootDirectory;

	public ZipFileWriter( final String fileName, final String rootDirectory ) throws MojoExecutionException {
		try {
			this.fileName = fileName;
			this.rootDirectory = rootDirectory;
			this.output = new ZipOutputStream( new FileOutputStream( fileName ) );
		} catch ( final FileNotFoundException e ) {
			throw new MojoExecutionException( MESSAGE_CANT_CREATE_ZIP, e );
		}
	}

	public void add( final String name, final InputStream content ) {
		try {
			output.putNextEntry( new ZipEntry( fixEntryName( name ) ) );

			final byte[] bytes = new byte[1024];
			int length;
			while ( ( length = content.read( bytes ) ) >= 0 )
				output.write( bytes, 0, length );

			output.closeEntry();
		} catch ( final IOException e ) {
			throw new RuntimeException( MESSAGE_CANT_ADD_TO_ZIP, e );
		}
	}

	String fixEntryName( String entryName ) {
		for ( final String prefix : prefixesToStripOutFromName )
			entryName = entryName.replaceFirst( prefix, "" );
		final String finalEntryName = rootDirectory + "/" + entryName;
		return finalEntryName;
	}

	public void close() throws MojoExecutionException {
		try {
			output.close();
		} catch ( final IOException e ) {
			throw new MojoExecutionException( MESSAGE_CANT_CREATE_ZIP, e );
		}
	}

	public void stripPrefix( final String... prefixes ) {
		for ( final String prefix : prefixes )
			prefixesToStripOutFromName.add( prefix );
	}
}