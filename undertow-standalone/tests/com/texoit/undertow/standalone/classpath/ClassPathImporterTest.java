package com.texoit.undertow.standalone.classpath;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.junit.Test;

public class ClassPathImporterTest {

	@Test
	public void grantThatFindClassesInTestDirectory() throws IOException{
		ClassPathImporter classPathImporter = new WebInfClassesDirectoryClassPathImporter( "./tests/", ".java" );
		List<Class<?>> classes = classPathImporter.search();
		assertThat( classes.size(), is( 1 ));
	}

	@Test
	public void grantThatFindClassesInsideJar() throws IOException{
		URLClassLoader classLoader = createClassLoader();
		try {
			ClassPathImporter classPathImporter = new JarClassPathImporter( "./tests/", ".class", classLoader );
			List<Class<?>> classes = classPathImporter.search();
			assertThat( classes.size(), is( 4 ));
		} finally {
			classLoader.close();
		}
	}

	private URLClassLoader createClassLoader() throws MalformedURLException {
		URL url = new File("./tests/sample.jar").toURI().toURL();
		URLClassLoader classLoader = new URLClassLoader( new URL[]{ url } );
		return classLoader;
	}
}
