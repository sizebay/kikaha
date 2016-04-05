package kikaha.config;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *
 */
public class ConfigLoaderTest {

	@Test
	public void ensureThatCanReadRootPathConfigurationFromYaml() throws IOException {
		final MergeableConfig defaultConfig = MergeableConfig.create().load(new File("tests-resources/conf/snippet1.yml"));
		final String name = defaultConfig.getString("name");
		assertEquals( "Kikaha", name );
	}

	@Test
	public void ensureThatCanReadMultiLevelPathConfigurationFromYaml() throws IOException {
		final MergeableConfig defaultConfig = MergeableConfig.create().load(new File("tests-resources/conf/snippet1.yml"));
		final String host = defaultConfig.getString("server.host");
		assertEquals( "0.0.0.0", host );
	}

	@Test
	public void ensureThatCanReadConfigurationListFromYaml() throws IOException {
		final MergeableConfig defaultConfig = MergeableConfig.create().load(new File("tests-resources/conf/snippet1.yml"));
		final List<Config> configList = defaultConfig.getConfigList("server.routes");
		assertNotNull( configList );
		assertEquals( 2l, configList.size() );
		assertEquals( "/", configList.get(0).getString("home.path")  );
	}

	@Test
	public void ensureThatCanReadStringListFromYaml() throws IOException {
		final MergeableConfig defaultConfig = MergeableConfig.create().load(new File("tests-resources/conf/snippet1.yml"));
		final List<String> configList = defaultConfig.getStringList("former-authors");
		assertNotNull( configList );
		assertEquals( 3l, configList.size() );
		assertEquals( "Miere", configList.get(0) );
		assertEquals( "Ricardo", configList.get(1) );
		assertEquals( "Cesar", configList.get(2) );
	}

	@Test
	public void ensureThatCanReadTheKeysOfYamlFile() throws IOException {
		final MergeableConfig defaultConfig = MergeableConfig.create().load(new File("tests-resources/conf/snippet1.yml"));
		final Set<String> keys = defaultConfig.getKeys();
		assertNotNull(keys);
		assertEquals(3, keys.size());
	}

	@Test
	public void ensureThatCanMergeYamlFiles() throws IOException {
		final MergeableConfig defaultConfig = MergeableConfig.create();
		defaultConfig.load(new File("tests-resources/conf/snippet1.yml"));
		assertNull( defaultConfig.getBoolean("server.user-ssl") );
		assertEquals( 9000, defaultConfig.getInteger("server.port").intValue() );
		assertEquals( "0.0.0.0", defaultConfig.getString("server.host") );

		defaultConfig.load(new File("tests-resources/conf/snippet2.yml"));
		assertTrue(defaultConfig.getBoolean("server.user-ssl") );
		assertEquals( 9001, defaultConfig.getInteger("server.port").intValue() );
		assertEquals( "0.0.0.0", defaultConfig.getString("server.host") );
	}

	@Test
	public void ensureThatCanReadAllConfigurationFilesFromConfigLoader() throws IOException {
		final Config config = ConfigLoader.loadDefaults();
		assertEquals( "Test", config.getString("server.name") );
		assertTrue( config.getBoolean( "server.defaults" ) );
		assertTrue( config.getBoolean( "server.application" ) );
		assertTrue( config.getBoolean( "server.application-test" ) );
	}
}
