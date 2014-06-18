package io.skullabs.undertow.urouting;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import trip.spi.helpers.filter.Filter;
import urouting.api.Path;

@SupportedAnnotationTypes( "urouting.api.*" )
public class MicroRoutingAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		try {
			System.out.println( "undertow-standalone::urouting processor" );
			System.out.println( roundEnv );
			generateRoutingMethods( roundEnv );
			return false;
		} catch ( IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	void generateRoutingMethods( RoundEnvironment roundEnv ) throws IOException {
		Iterable<Element> elementsAnnotatedWith = retrieveMethodsAnnotatedWith( roundEnv, Path.class );
		for ( Element method : elementsAnnotatedWith )
			generateRoutingMethods( (ExecutableElement)method, roundEnv );
	}

	void generateRoutingMethods( ExecutableElement method, RoundEnvironment roundEnv ) {

	}

	@SuppressWarnings( "unchecked" )
	Iterable<Element> retrieveMethodsAnnotatedWith( RoundEnvironment roundEnv, Class<? extends Annotation> annotation )
			throws IOException {
		return (Iterable<Element>)Filter.filter(
				roundEnv.getElementsAnnotatedWith( annotation ),
				new MethodsOnlyCondition() );
	}

	Filer filer() {
		return this.processingEnv.getFiler();
	}

	/**
	 * We just return the latest version of whatever JDK we run on. Stupid?
	 * Yeah, but it's either that or warnings on all versions but 1. Blame Joe.
	 * 
	 * PS: this method was copied from Project Lombok. ;)
	 */
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.values()[SourceVersion.values().length - 1];
	}

}
