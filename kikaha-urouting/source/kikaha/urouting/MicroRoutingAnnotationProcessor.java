package kikaha.urouting;

import static kikaha.urouting.AnnotationProcessorUtil.retrieveMethodsAnnotatedWith;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import kikaha.urouting.api.DELETE;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.MultiPartFormData;
import kikaha.urouting.api.PATCH;
import kikaha.urouting.api.POST;
import kikaha.urouting.api.PUT;

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
		final List<Element> elementsAnnotatedWith = retrieveMethodsAnnotatedWith( roundEnv, httpMethodAnnotation );
		if ( !elementsAnnotatedWith.isEmpty() )
			log( "Creating Undertow routes for HTTP methods" );
		for ( final Element method : elementsAnnotatedWith )
			generateRoutingMethods( (ExecutableElement)method, roundEnv, httpMethodAnnotation );
	}

	void generateRoutingMethods( final ExecutableElement method, final RoundEnvironment roundEnv,
			final Class<? extends Annotation> httpMethodAnnotation ) throws IOException {
		final RoutingMethodData routingMethodData = RoutingMethodData.from( method, httpMethodAnnotation );
		log( " > method " + routingMethodData );
		generator.generate( routingMethodData );
	}

	protected void log(final String msg) {
		System.out.println( "[INFO] " + msg );
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
