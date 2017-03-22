package kikaha.apt;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

/**
 * @author: miere.teixeira
 */
public abstract class AbstractAnnotatedMethodProcessor extends AnnotationProcessor {

	protected ClassGenerator generator;

	@Override
	public synchronized void init( ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		generator = new ClassGenerator( processingEnv.getFiler(), getTemplateName() );
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

	void generateMethods( final RoundEnvironment roundEnv, final Class<? extends Annotation> annotation ) throws
			IOException {
		final List<ExecutableElement> elementsAnnotatedWith = APT.retrieveMethodsAnnotatedWith( roundEnv, annotation );
		for ( final ExecutableElement method : elementsAnnotatedWith )
			generateMethod( method, roundEnv, annotation );
	}

	protected abstract void generateMethod(
			ExecutableElement method, RoundEnvironment roundEnv, Class<? extends Annotation> annotation )
			throws IOException;
}
