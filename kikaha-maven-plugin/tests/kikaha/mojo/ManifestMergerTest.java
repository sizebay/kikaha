package kikaha.mojo;

import kikaha.mojo.packager.ManifestMerger;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by miere.teixeira on 05/12/2016.
 */
public class ManifestMergerTest {

    final String expectedMergedManifest = "Manifest-Version: 1.0\r\n" +
            "Bundle-Description: sample\r\n" +
            "Bnd-LastModified: 6150020147525\r\n" +
            "Created-By: 1.4.0 (Sun Microsystems Inc.)\r\n" +
            "\r\n";

    @Test
    public void ensureCanMergeManifestFiles() throws IOException {
        final ManifestMerger merger = new ManifestMerger( "META-INF/MANIFEST.MF" );
        merger.add( getClass().getResourceAsStream( "/MANIFEST1.MF" ) );
        merger.add( getClass().getResourceAsStream( "/MANIFEST2.MF" ) );
        assertEquals( expectedMergedManifest, merger.merge() );
    }
}
