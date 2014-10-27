package kikaha.mojo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.MojoExecutionException;

public class ZipFileWriter {

	static final String MESSAGE_CANT_CREATE_ZIP = "Can't create zip file";
	static final String MESSAGE_CANT_ADD_TO_ZIP = "Can't add file to zip";

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
		return rootDirectory + "/" + entryName;
	}

	public void close() throws MojoExecutionException {
		try {
			output.close();
			System.out.println( "Success: Zip file generated at " + this.fileName );
		} catch ( final IOException e ) {
			throw new MojoExecutionException( MESSAGE_CANT_CREATE_ZIP, e );
		}
	}

	public void stripPrefix( final String... prefixes ) {
		for ( final String prefix : prefixes )
			prefixesToStripOutFromName.add( prefix );
	}
}