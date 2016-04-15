package kikaha.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a configuration read from a file.
 * Based on the well known Typesafe's Config Library.
 */
public interface Config {

	/**
	 * Retrieve a configuration String from a file.
	 *
	 * @param path which configuration should be read
	 * @return a configuration String.
	 */
	String getString(String path);

	String getString(String path, String defaultValue);

	/**
	 * Retrieve a configuration Integer from a file.
	 *
	 * @param path which configuration should be read
	 * @return a configuration Integer
	 */
	Integer getInteger(String path);


	/**
	 * Retrieve a Boolean parameter from configuration.
	 *
	 * @param path which configuration should be read
	 * @return a Boolean parameter from configuration
	 */
	Boolean getBoolean(String path);

	/**
	 * Retrieve a list of configurations from a file. It is useful to deal with
	 * nested structures.
	 *
	 * @param path which configuration should be read
	 * @return a list of configurations from a file
	 */
	List<Config> getConfigList(String path);


	/**
	 * Retrieve a list of strings from a file.
	 *
	 * @param path which configuration should be read
	 * @return a list of strings from a file
	 */
	List<String> getStringList(String path);

	/**
	 * Retrieve the key list available on a configuration.
	 * @return a key list available on a configuration
	 */
	Set<String> getKeys();

	Config getConfig(String path);

	Object getObject(String path);

	Class<?> getClass( String path );

	Map<String, Object> toMap();
}
