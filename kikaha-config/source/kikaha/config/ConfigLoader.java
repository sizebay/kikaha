package kikaha.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 *
 */
@Slf4j
public abstract class ConfigLoader {

	public static Config loadDefaults() {
		try {
			final MergeableConfig config = MergeableConfig.create();
			loadFiles(config, ClassLoader.getSystemResources("META-INF/defaults.yml"));
			loadFiles(config, ClassLoader.getSystemResources("conf/application.yml"));
			loadFiles(config, ClassLoader.getSystemResources("conf/application-test.yml"));
			return config;
		} catch ( IOException cause ) {
			throw new IllegalStateException(cause);
		}
	}

	private static void loadFiles( MergeableConfig config, Enumeration<URL> resources ) throws IOException {
		while( resources.hasMoreElements() ){
			final URL url = resources.nextElement();
			try (final InputStream stream = url.openStream() ) {
				log.info( "Loading configurations from: " + url.getPath() );
				config.load(stream);
			}
		}
	}
}
