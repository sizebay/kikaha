package kikaha.mojo.packager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

/**
 * Created by miere.teixeira on 05/12/2016.
 */
@Getter
@RequiredArgsConstructor
public class ManifestMerger implements Packager.FileMerger {

    final Manifest manifest = new Manifest();
    final String fileName;

    @Override
    public void add(InputStream inputStream) throws IOException {
        manifest.read( inputStream );
    }

    @Override
    public String merge() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manifest.write( out );
        return out.toString();
    }
}
