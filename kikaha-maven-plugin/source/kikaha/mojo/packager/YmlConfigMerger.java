package kikaha.mojo.packager;

import kikaha.config.MergeableConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by miere.teixeira on 05/12/2016.
 */
@Getter
@RequiredArgsConstructor
public class YmlConfigMerger implements Packager.FileMerger {

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
