package kikaha.mojo.packager;

import static kikaha.mojo.packager.packager.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import lombok.*;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Created by miere.teixeira on 05/12/2016.
 */
@Getter
@RequiredArgsConstructor
public class JarWriter {

    final Set<String> jarFiles = new HashSet<>();
    final ZipOutputStream output;
    final Map<String, FileMerger> mergers;
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
            final FileMerger writable = mergers.computeIfAbsent( name,
                n-> n.startsWith(SERVICE_FILE_NAME) ? new SimpleMerger(n) : null );
            if ( writable != null )
               writable.add( content );
            else if ( !jarFiles.contains( name ) )
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
            jarFiles.add( name );
        } catch ( final IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( MESSAGE_CANT_ADD_TO_ZIP + " ("+name+")", e );
        }
    }

    public void flush() throws MojoExecutionException {
        try {
            for (final FileMerger merger : mergers.values()) {
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
            e.printStackTrace();
            throw new RuntimeException( MESSAGE_CANT_ADD_TO_ZIP + " ("+name+")", e );
        }
    }


}
