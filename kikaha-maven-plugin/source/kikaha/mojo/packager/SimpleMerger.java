package kikaha.mojo.packager;

import static kikaha.mojo.packager.packager.copy;
import java.io.*;
import lombok.*;

/**
 * Created by miere.teixeira on 06/12/2016.
 */
@Getter
@RequiredArgsConstructor
public class SimpleMerger implements FileMerger {

    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final String fileName;

    @Override
    public void add(InputStream inputStream) throws IOException {
        copy( inputStream, buffer::write );
        copy( new ByteArrayInputStream( "\n".getBytes() ), buffer::write );
    }

    @Override
    public String merge() throws IOException {
        return buffer.toString();
    }
}
