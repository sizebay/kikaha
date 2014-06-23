package io.skullabs.undertow.urouting;

import io.skullabs.undertow.urouting.api.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import trip.spi.helpers.filter.Filter;

@SupportedAnnotationTypes( "urouting.api.*" )
public class MicroRoutingAnnotationProcessor extends AbstractProcessor {

	RoutingMethodClassGenerator generator;

	@Override
	public synchronized void init( ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		generator = new RoutingMethodClassGenerator( filer() );
	}

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		try {
			System.out.println( "urouting processor::" + roundEnv );
			generateRoutingMethods( roundEnv, GET.class );
			generateRoutingMethods( roundEnv, POST.class );
			generateRoutingMethods( roundEnv, PUT.class );
			generateRoutingMethods( roundEnv, DELETE.class );
			return false;
		} catch ( IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	void generateRoutingMethods( RoundEnvironment roundEnv, Class<? extends Annotation> httpMethodAnnotation ) throws IOException {
		Iterable<Element> elementsAnnotatedWith = retrieveMethodsAnnotatedWith( roundEnv, httpMethodAnnotation );
		for ( Element method : elementsAnnotatedWith )
			generateRoutingMethods( (ExecutableElement)method, roundEnv, httpMethodAnnotation );
	}

	void generateRoutingMethods( ExecutableElement method, RoundEnvironment roundEnv,
			Class<? extends Annotation> httpMethodAnnotation ) throws IOException {
		System.out.println( "Generating class for method " + method );
		generator.generate( RoutingMethodData.from( method, httpMethodAnnotation ) );
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
