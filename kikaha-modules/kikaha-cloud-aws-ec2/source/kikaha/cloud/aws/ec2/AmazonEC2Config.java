package kikaha.cloud.aws.ec2;

import java.util.*;
import kikaha.config.Config;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@RequiredArgsConstructor
public class AmazonEC2Config implements Config {

	final Map<String, String> tags;
	final Config fallback;

	@Override
	public String getString(String path) {
		final String value = tags.get(path);
		if ( value == null )
			return fallback.getString(path);
		return value;
	}

	@Override
	public String getString(String path, String defaultValue) {
		final String value = tags.get(path);
		if ( value == null )
			return fallback.getString(path, defaultValue);
		return value;
	}

	@Override
	public byte[] getBytes(String path, String defaultValue) {
		final String value = tags.get(path);
		if ( value == null )
			return fallback.getBytes(path, defaultValue);
		return value.getBytes();
	}

	@Override
	public int getInteger(String path, int defaultValue) {
		final String value = tags.get(path);
		if ( value == null )
			return fallback.getInteger(path, defaultValue);
		return Integer.valueOf(value);
	}

	@Override
	public long getLong(String path, long defaultValue) {
		final String value = tags.get(path);
		if ( value == null )
			return fallback.getLong(path, defaultValue);
		return Long.valueOf(value);
	}

	@Override
	public boolean getBoolean(String path, boolean defaultValue) {
		final String value = tags.get(path);
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
		return new AmazonEC2Config( tags, config );
	}

	@Override
	public Object getObject(String path) {
		final String value = tags.get(path);
		if ( value == null )
			return fallback.getObject( path );
		return value;
	}

	@Override
	public Class<?> getClass(String path, Class<?> defaultClass) {
		final String value = tags.get(path);
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
		map.putAll( tags );
		return map;
	}
}
