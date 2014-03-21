package com.texoit.undertow.standalone;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.texoit.undertow.standalone.api.Configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class LibraryClassPathImporter {

	private static final String WEB_APPLICATION_LIBRARIES_DIR = "/WEB-INF/lib";
	private static final String CLASS_EXTENSION = ".class";

	private final List<Class<?>> classes = new ArrayList<>();
	private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	private final Configuration configuration;

	public List<Class<?>> retrieve() throws IOException {
		searchForJars( configuration.libraryPath() );
		searchForJars( configuration.resourcesPath() + WEB_APPLICATION_LIBRARIES_DIR );
		return classes();
	}

	public void searchForJars( String directory ) throws IOException {
		searchForJars( new File( directory ) );
	}

	public void searchForJars( File directory ) throws IOException {
		if ( directory.exists() && directory.isDirectory() )
			for ( String fileName : directory.list() )
				memorizeFileOrRecursivelySearchAgain(directory, fileName);
	}

	private void memorizeFileOrRecursivelySearchAgain(File directory, String fileName) throws IOException  {
		if ( fileName.endsWith(".jar") )
			memorizeJar(directory, fileName);
		else
			searchForJars(fileName);
	}

	private void memorizeJar(File directory, String fileName) throws IOException {
		String path = directory.getAbsolutePath() + File.separatorChar + fileName;
		JarFile filePath = new JarFile( path );
		Enumeration<JarEntry> entries = filePath.entries();
		while ( entries.hasMoreElements() ) {
			JarEntry jarEntry = entries.nextElement();
			memorizeJarEntry(jarEntry);
		}
		filePath.close();
	}

	private void memorizeJarEntry( JarEntry jarEntry ) {
		final String name = jarEntry.getName();
		if ( name.endsWith(CLASS_EXTENSION) )
			memorizeClass(parse(name));
	}

	private void memorizeClass(final String parsedName) {
		try {
			Class<?> loadedClass = classLoader().loadClass(parsedName);
			classes().add(loadedClass);
		} catch (ClassNotFoundException e) {
		}
	}

	private String parse(final String name) {
		return name.replace(CLASS_EXTENSION, "")
					.replace(File.pathSeparatorChar, '.')
					.replace('/', '.')
					.replace('$', '.');
	}
}
