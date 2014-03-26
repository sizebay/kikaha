package com.texoit.undertow.standalone.classpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class JarClassPathImporter extends ClassPathImporter {

	final List<Class<?>> classes = new ArrayList<>();
	final String rootDirectory;
	final String fileExtension;
	final ClassLoader classLoader;

	public static JarClassPathImporter from( String rootDirectory ) {
		return new JarClassPathImporter( rootDirectory, CLASS_EXTENSION, ClassLoader.getSystemClassLoader() );
	}

	public List<Class<?>> search() throws IOException{
		searchForJars( rootDirectory() );
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
		if ( name.endsWith( fileExtension() ) )
			memorizeClass(parse(name));
	}
}
