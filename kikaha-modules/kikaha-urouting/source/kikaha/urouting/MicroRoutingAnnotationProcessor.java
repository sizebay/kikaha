package kikaha.urouting;

import static kikaha.core.cdi.helpers.filter.AnnotationProcessorUtil.retrieveMethodsAnnotatedWith;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;
import kikaha.urouting.api.*;

@SupportedAnnotationTypes( "kikaha.urouting.api.*" )
public class MicroRoutingAnnotationProcessor extends AbstractProcessor {

	RoutingMethodClassGenerator generator;

	@Override
	public synchronized void init( final ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		generator = new RoutingMethodClassGenerator( filer() );
	}

	@Override
	public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv ) {
		try {
			generateRoutingMethods( roundEnv, GET.class );
			generateRoutingMethods( roundEnv, POST.class );
			generateRoutingMethods( roundEnv, PUT.class );
			generateRoutingMethods( roundEnv, DELETE.class );
			generateRoutingMethods( roundEnv, PATCH.class );
			generateRoutingMethods( roundEnv, MultiPartFormData.class );
			return false;
		} catch ( final IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	void generateRoutingMethods( final RoundEnvironment roundEnv, final Class<? extends Annotation> httpMethodAnnotation ) throws IOException {
		final List<ExecutableElement> elementsAnnotatedWith = retrieveMethodsAnnotatedWith( roundEnv, httpMethodAnnotation );
		if ( !elementsAnnotatedWith.isEmpty() )
			info( "Found HTTP methods to generate Undertow Routes" );
		for ( final ExecutableElement method : elementsAnnotatedWith )
			generateRoutingMethods( method, roundEnv, httpMethodAnnotation );
	}

	void generateRoutingMethods( final ExecutableElement method, final RoundEnvironment roundEnv,
			final Class<? extends Annotation> httpMethodAnnotation ) throws IOException {
		final RoutingMethodData routingMethodData = RoutingMethodData.from( method, httpMethodAnnotation );
		info( " " + routingMethodData );
		generator.generate( routingMethodData );
	}

	private void info( final String msg ) {
		processingEnv.getMessager().printMessage( Kind.NOTE, msg );
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
