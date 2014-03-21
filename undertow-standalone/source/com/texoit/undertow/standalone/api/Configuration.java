package com.texoit.undertow.standalone.api;

public interface Configuration {

	String libraryPath();

	String resourcesPath();
	
	String currentWorkDir();

	Integer port();

	String host();

}