package kikaha.mojo.packager;

import java.io.*;
import kikaha.config.MergeableConfig;
import lombok.*;

/**
 * Created by miere.teixeira on 05/12/2016.
 */
@Getter
@RequiredArgsConstructor
public class YmlConfigMerger implements FileMerger {

    final MergeableConfig config = MergeableConfig.create();
    final String fileName;

    @Override
    public void add(InputStream inputStream) throws IOException {
        config.load( inputStream );
    }

    @Override
    public String merge() throws IOException {
        return config.toString();
    }
}
