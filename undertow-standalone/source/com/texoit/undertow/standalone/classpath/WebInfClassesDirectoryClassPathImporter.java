package com.texoit.undertow.standalone.classpath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class WebInfClassesDirectoryClassPathImporter extends ClassPathImporter {

	private final List<Class<?>> classes = new ArrayList<>();
	private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	final String rootDirectory;
	final String fileExtension;

	public static WebInfClassesDirectoryClassPathImporter from( String rootDirectory ) {
		return new WebInfClassesDirectoryClassPathImporter(rootDirectory, CLASS_EXTENSION );
	}
	
	/* (non-Javadoc)
	 * @see com.texoit.undertow.standalone.ClassPathImporter#searchForClasses()
	 */
	@Override
	public List<Class<?>> search() {
		searchForClasses( rootDirectory );
		return classes();
	}

	public void searchForClasses( String rootDirectoryPath ) {
		File directory = new File( rootDirectoryPath );
		searchForClasses( rootDirectoryPath, directory );
	}

	public void searchForClasses( String rootDirectoryPath, File directory ) {
		if ( directory.exists() && directory.isDirectory() )
			for ( String fileName : directory.list() )
				memorizeClassOrRecursivelySearchAgain( rootDirectoryPath + File.separatorChar + fileName);
	}

	private void memorizeClassOrRecursivelySearchAgain(String fileName) {
		if ( fileName.endsWith( fileExtension() ) )
			memorizeClass( parse(fileName) );
		else
			searchForClasses( fileName );
	}
}
