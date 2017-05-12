package kikaha.cloud.aws.ec2;

import java.util.*;
import java.util.function.Supplier;
import kikaha.config.Config;
import lombok.*;

/**
 *
 */
@RequiredArgsConstructor
public class AmazonEC2Config implements Config {

	final Supplier<Map<String, String>> tagProducer;
	final Config fallback;

	@Getter(lazy = true)
	private final Map<String, String> tags = tagProducer.get();

	@Override
	public String getString(String path) {
		final String value = getTags().get(path);
		if ( value == null )
			return fallback.getString(path);
		return value;
	}

	@Override
	public String getString(String path, String defaultValue) {
		final String value = getTags().get(path);
		if ( value == null )
			return fallback.getString(path, defaultValue);
		return value;
	}

	@Override
	public byte[] getBytes(String path, String defaultValue) {
		final String value = getTags().get(path);
		if ( value == null )
			return fallback.getBytes(path, defaultValue);
		return value.getBytes();
	}

	@Override
	public int getInteger(String path, int defaultValue) {
		final String value = getTags().get(path);
		if ( value == null )
			return fallback.getInteger(path, defaultValue);
		return Integer.valueOf(value);
	}

	@Override
	public long getLong(String path, long defaultValue) {
		final String value = getTags().get(path);
		if ( value == null )
			return fallback.getLong(path, defaultValue);
		return Long.valueOf(value);
	}

	@Override
	public boolean getBoolean(String path, boolean defaultValue) {
		final String value = getTags().get(path);
		if ( value == null )
			return fallback.getBoolean(path, defaultValue);
		return Boolean.valueOf(value);
	}

	@Override
	public List<Config> getConfigList(String path) {
		return fallback.getConfigList(path);
	}

	@Override
	public List<String> getStringList(String path, List<String> defaultValues) {
		return fallback.getStringList( path, defaultValues);
	}

	@Override
	public Set<String> getKeys() {
		return fallback.getKeys();
	}

	@Override
	public Config getConfig(String path) {
		final Config config = fallback.getConfig(path);
		if ( config == null )
			return null;

		final Map<String, String> newTags = new HashMap<>();
		for ( String key : getTags().keySet() )
			if ( key.startsWith( path ) )
				newTags.put( key.replace( path + ".", "" ), getTags().get(key) );

		return new AmazonEC2Config( ()->newTags, config );
	}

	@Override
	public Object getObject(String path) {
		final String value = getTags().get(path);
		if ( value == null )
			return fallback.getObject( path );
		return value;
	}

	@Override
	public Class<?> getClass(String path, Class<?> defaultClass) {
		final String value = getTags().get(path);
		if ( value == null )
			return fallback.getClass( path, defaultClass );
		return instantiate( value );
	}

	private Class<?> instantiate( String className ) {
		try {
			return Class.forName( className );
		} catch ( Throwable cause ) {
			throw new IllegalStateException( "Can't load " + className, cause);
		}
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = fallback.toMap();
		map.putAll( getTags() );
		return map;
	}
}
