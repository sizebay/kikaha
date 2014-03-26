package com.texoit.undertow.standalone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.texoit.undertow.standalone.api.Configuration;
import com.texoit.undertow.standalone.classpath.ClassPathImporter;
import com.texoit.undertow.standalone.classpath.JarClassPathImporter;
import com.texoit.undertow.standalone.classpath.WebInfClassesDirectoryClassPathImporter;

@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class LibraryClassPathImporter {

	private static final String WEB_APPLICATION_CLASSES_DIR = "/WEB-INF/classes";
	private static final String WEB_APPLICATION_LIBRARIES_DIR = "/WEB-INF/lib";

	private final List<Class<?>> classes = new ArrayList<>();
	private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	private final Configuration configuration;

	public List<Class<?>> retrieve() throws IOException {
		searchForJars( configuration.libraryPath() );
		searchForJars( configuration.resourcesPath() + WEB_APPLICATION_LIBRARIES_DIR );
		searchForClasses( configuration.resourcesPath() + WEB_APPLICATION_CLASSES_DIR );
		return classes();
	}

	public void searchForJars( String directory ) throws IOException {
		ClassPathImporter importer = JarClassPathImporter.from( directory );
		classes.addAll( importer.search() );
	}

	public void searchForClasses( String directory ) throws IOException {
		ClassPathImporter importer = WebInfClassesDirectoryClassPathImporter.from( directory );
		classes.addAll( importer.search() );
	}
}
