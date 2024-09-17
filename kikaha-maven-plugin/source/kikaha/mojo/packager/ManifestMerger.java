package kikaha.mojo.packager;

import java.util.jar.Manifest;
import java.io.*;
import lombok.*;

/**
 * Created by miere.teixeira on 05/12/2016.
 */
@Getter
@RequiredArgsConstructor
public class ManifestMerger implements FileMerger {

    final String fileName;

    @Override
    public void add(InputStream inputStream) throws IOException {}

    @Override
    public String merge() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream("/META-INF/DEFAULT-MANIFEST.MF");
        final Manifest manifest = new Manifest( inputStream );
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        manifest.write( out );
        out.flush();
        return out.toString();
    }
}
