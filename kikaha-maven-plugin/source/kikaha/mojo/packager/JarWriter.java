package kikaha.mojo.packager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static kikaha.mojo.packager.Packager.MESSAGE_CANT_ADD_TO_ZIP;
import static kikaha.mojo.packager.Packager.copy;

/**
 * Created by miere.teixeira on 05/12/2016.
 */
@Getter
@RequiredArgsConstructor
public class JarWriter {

    final ZipOutputStream output;
    final Map<String, Packager.FileMerger> mergers;
    final String fileName;

    public JarWriter( final String fileName ) throws FileNotFoundException {
        mergers = new HashMap<>();
        mergers.put( "META-INF/MANIFEST.MF", new ManifestMerger( "META-INF/MANIFEST.MF" ) );
        mergers.put( "META-INF/defaults.yml", new YmlConfigMerger( "META-INF/defaults.yml" ) );
        mergers.put( "conf/application.yml", new YmlConfigMerger( "conf/application.yml" ) );
        this.output = new ZipOutputStream( new FileOutputStream( fileName ) );
        this.fileName = fileName;
    }

    public void mergeJar(final String path) throws MojoExecutionException {
        try (final ZipFileReader reader = new ZipFileReader(path.replace("%20", " "))) {
            try {
                reader.read( this::addFile );
            } catch (IOException e) {
                throw new MojoExecutionException( MESSAGE_CANT_ADD_TO_ZIP, e );
            }
        }
    }

    public void addFile( final String name, final InputStream content ) {
        try {
            final Packager.FileMerger writable = mergers.get( name );
            if ( writable != null )
               writable.add( content );
            else
                add( name, content );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public void add( final String name, final InputStream content ) {
        try {
            output.putNextEntry( new ZipEntry( name ) );
            copy( content, output::write );
            output.closeEntry();
        } catch ( final IOException e ) {
            throw new RuntimeException( MESSAGE_CANT_ADD_TO_ZIP, e );
        }
    }

    public void flush() throws MojoExecutionException {
        try {
            for (Packager.FileMerger merger : mergers.values()) {
                final String merged = merger.merge();
                add(merger.getFileName(), merged);
            }
            output.close();
        } catch (IOException e) {
            throw new MojoExecutionException( MESSAGE_CANT_ADD_TO_ZIP, e );
        }
    }

    void add( final String name, final String content ) {
        try {
            output.putNextEntry( new ZipEntry( name ) );
            final byte[] bytes = content.getBytes();
            output.write( bytes, 0, bytes.length );
            output.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException( MESSAGE_CANT_ADD_TO_ZIP, e );
        }
    }


}
