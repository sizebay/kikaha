package kikaha.uworkers.core;

import static kikaha.apt.APT.*;
import java.io.IOException;
import java.util.*;
import javax.annotation.processing.*;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import kikaha.apt.*;
import kikaha.uworkers.api.*;

/**
 *
 */
@Singleton
@SupportedAnnotationTypes( "kikaha.uworkers.api.*" )
public class MicroWorkerAnnotationProcessor extends AnnotationProcessor implements Processor {

	ClassGenerator generator;

	@Override
	public synchronized void init( final ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		generator = new ClassGenerator( processingEnv.getFiler(), "worker-listener-class.mustache" );
	}

	@Override
	public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv ) {
		try {
			final List<ExecutableElement> elements = retrieveMethodsAnnotatedWith( roundEnv, Worker.class );
			if ( !elements.isEmpty() )
				process( elements );
			return false;
		} catch ( final IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	private void process( final List<ExecutableElement> elements ) throws IOException {
		info( "Found " + elements.size() + " worker methods..." );
		for ( ExecutableElement element : elements ) {
			final MicroWorkerListenerClass clazz = createClassFrom( element );
			if ( clazz != null ) {
				info("  > " + clazz.getGeneratedClassCanonicalName() );
				generator.generate( clazz );
			}
		}
	}

	private MicroWorkerListenerClass createClassFrom( final ExecutableElement element ) {
		final String type = APT.asType( element.getEnclosingElement() );
		final List<? extends VariableElement> typeParameters = element.getParameters();
		if ( typeParameters.size() > 1 || typeParameters.isEmpty() )
			throw new IllegalArgumentException("Invalid method " + type + "." + element.toString() + ". Worker methods should have exactly one argument.");

		final String parameterType = extractCanonicalName( typeParameters.get(0) );

		return new MicroWorkerListenerClass(
				APT.extractPackageName( type ), APT.extractTypeName( type ), element.getSimpleName().toString(), parameterType,
				element.getAnnotation(Worker.class).value(),
				!parameterType.equals(Exchange.class.getCanonicalName()) );
	}

}
