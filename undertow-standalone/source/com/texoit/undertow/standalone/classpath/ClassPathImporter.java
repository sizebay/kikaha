package com.texoit.undertow.standalone.classpath;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class ClassPathImporter {

	public static final String CLASS_EXTENSION = ".class";

	public abstract String rootDirectory();
	public abstract String fileExtension();
	public abstract List<Class<?>> classes();
	public abstract ClassLoader classLoader();
	
	public abstract List<Class<?>> search() throws IOException;

	protected void memorizeClass(final String parsedName) {
		try {
			Class<?> loadedClass = classLoader().loadClass(parsedName);
			classes().add(loadedClass);
		} catch (ClassNotFoundException e) {
//			System.out.println( "FAILED:" + parsedName );
		}
	}

	protected String parse(final String name) {
		return name.replace(rootDirectory() + File.separatorChar, "")
					.replace(rootDirectory(), "")
					.replace(fileExtension(), "")
					.replace(File.separatorChar, '.')
					.replace('/', '.')
					.replace('$', '.');
	}

}