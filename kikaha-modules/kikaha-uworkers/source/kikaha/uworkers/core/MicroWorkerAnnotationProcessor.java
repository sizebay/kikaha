package kikaha.uworkers.core;

import static kikaha.core.cdi.helpers.filter.AnnotationProcessorUtil.*;
import java.io.IOException;
import java.util.*;
import javax.annotation.processing.*;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import kikaha.uworkers.api.*;

/**
 *
 */
@Singleton
@SupportedAnnotationTypes( "kikaha.uworkers.api.*" )
public class MicroWorkerAnnotationProcessor extends AbstractProcessor implements Processor {

	MicroWorkerClassGenerator generator;

	@Override
	public synchronized void init( final ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		generator = new MicroWorkerClassGenerator( processingEnv.getFiler() );
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
				info("  Generating " + clazz.getTargetCanonicalClassName() );
				generator.generate( clazz );
			}
		}
	}

	private MicroWorkerListenerClass createClassFrom( final ExecutableElement element ) {
		final List<? extends VariableElement> typeParameters = element.getParameters();
		if ( typeParameters.size() > 1 || typeParameters.isEmpty() ) {
			final String className = extractCanonicalName(element.getEnclosingElement());
			throw new IllegalArgumentException("Invalid method " + className + "." + element.toString() + ". Worker methods should have exactly one argument.");
		}

		final String parameterType = extractCanonicalName( typeParameters.get(0) );
		return createClass( element, element.getAnnotation(Worker.class),
			extractPackageName( element.getEnclosingElement() ), parameterType,
			!parameterType.equals(Exchange.class.getCanonicalName()) );
	}

	private MicroWorkerListenerClass createClass(
			final ExecutableElement element, final Worker workerAnnotation,
			final String packageName, final String parameterType, final boolean isRawObject )
	{
		return new MicroWorkerListenerClass(
				packageName,
				extractCanonicalName( element.getEnclosingElement() ).replace( packageName + ".", "" ),
				element.getSimpleName().toString(),
				parameterType,
				workerAnnotation.value(),
				isRawObject );
	}

	private void info( final String msg ) {
		processingEnv.getMessager().printMessage( Diagnostic.Kind.NOTE, msg );
	}

	private void warn( final String msg ) {
		processingEnv.getMessager().printMessage( Diagnostic.Kind.MANDATORY_WARNING, msg );
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
