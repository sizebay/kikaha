package kikaha.protobuf;

import static kikaha.core.cdi.helpers.filter.AnnotationProcessorUtil.retrieveMethodsAnnotatedWith;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

/**
 * @author: miere.teixeira
 */
public abstract class AbstractAnnotatedMethodProcessor extends AbstractProcessor {

	// TODO: make this constant public on AnnotationProcessorUtil.java
	private static final String METHOD_PARAM_EO = "\n\t\t\t";

	AnnotatedMethodClassGenerator generator;

	@Override
	public synchronized void init( ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		generator = new AnnotatedMethodClassGenerator( processingEnv.getFiler(), getTemplateName() );
	}

	protected abstract String getTemplateName();

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		try {
			final List<Class<? extends Annotation>> annotationsThatShouldBePresentOnMethod = getExpectedMethodAnnotations();
			for ( final Class<? extends Annotation> annotation : annotationsThatShouldBePresentOnMethod )
				generateMethods( roundEnv, annotation );
			return false;
		} catch ( IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	protected abstract List<Class<? extends Annotation>> getExpectedMethodAnnotations();

	void generateMethods( final RoundEnvironment roundEnv, final Class<? extends Annotation> annotation ) throws IOException {
		final List<ExecutableElement> elementsAnnotatedWith = retrieveMethodsAnnotatedWith( roundEnv, annotation );
		if ( !elementsAnnotatedWith.isEmpty() )
			info( "Found HTTP methods to generate Undertow Routes" );
		for ( final ExecutableElement method : elementsAnnotatedWith )
			generateMethod( method, roundEnv, annotation );
	}

	protected String extractMethodParamsFrom( final ExecutableElement method, BiFunction<ExecutableElement, VariableElement, String> methodParser ) {
		final StringBuilder buffer = new StringBuilder();
		for ( final VariableElement parameter : method.getParameters() ) {
			if ( buffer.length() > 0  )
				buffer.append( ',' );
			buffer.append( METHOD_PARAM_EO ).append( methodParser.apply( method, parameter ) );
		}
		return buffer.toString();
	}

	protected abstract void generateMethod(
		ExecutableElement method, RoundEnvironment roundEnv, Class<? extends Annotation> annotation )
			throws IOException;


	protected String extractMethodParamFrom( ExecutableElement method, VariableElement parameter ) {
		final Map<Function<VariableElement, Boolean>, Function<VariableElement, String>> rules = getMethodRules();
		for ( final Entry<Function<VariableElement, Boolean>, Function<VariableElement, String>> rule : rules.entrySet()  ) {
			if ( rule.getKey().apply( parameter ) )
				return rule.getValue().apply( parameter );
		}
		return extractParamFromNonAnnotatedParameter( method, parameter );
	}

	protected abstract String extractParamFromNonAnnotatedParameter( ExecutableElement method, VariableElement parameter );

	protected abstract Map<Function<VariableElement,Boolean>,Function<VariableElement,String>> getMethodRules();

	protected void info( final String msg ) {
		processingEnv.getMessager().printMessage( Kind.NOTE, msg );
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
