package kikaha.apt;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import javax.annotation.processing.RoundEnvironment;
import javax.enterprise.inject.Typed;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import kikaha.core.cdi.helpers.filter.*;

public interface APT {

	String METHOD_PARAM_EOL = "\n\t\t\t";

	@SuppressWarnings( "unchecked" )
	static List<ExecutableElement> retrieveMethodsAnnotatedWith( final RoundEnvironment roundEnv,
			final Class<? extends Annotation> annotation )
	{
		return (List) asList((Iterable<Element>) Filter.filter(
				roundEnv.getElementsAnnotatedWith( annotation ),
				new MethodsOnlyCondition() ));
	}

	static <T> List<T> asList( Iterable<T> items ){
		final List<T> list = new ArrayList<>();
		for ( final T item : items )
			list.add(item);
		return list;
	}

	static ExecutableElement retrieveFirstMethodAnnotatedWith( final TypeElement typeElement,
			final Class<? extends Annotation> annotation )
	{
		return (ExecutableElement)Filter.first(
				typeElement.getEnclosedElements(),
				new AnnotatedMethodsCondition( annotation ) );
	}

	static String extractServiceInterfaceFrom( final ExecutableElement method ) {
		final TypeElement classElement = (TypeElement)method.getEnclosingElement();
		final String clazzName = extractServiceInterfaceFrom(classElement);
		if ( clazzName != null )
			return clazzName;
		return classElement.asType().toString();
	}

	static String extractServiceInterfaceFrom( final TypeElement classElement ) {
		final Typed typedAnn = classElement.getAnnotation(Typed.class);
		try {
			if ( typedAnn != null ) {
				final Class<?>[] classes = typedAnn.value();
				classes[0].getCanonicalName();
				return null;
			}
			return classElement.asType().toString().replaceAll("<[^>]+>","");
		} catch ( MirroredTypesException cause ) {
			final TypeMirror typeMirror = cause.getTypeMirrors().get(0);
			return typeMirror.toString().replaceAll("<[^>]+>","");
		}
	}

	static String extractCanonicalName( final Element element ) {
		return element.asType().toString();
	}

	static String extractPackageName( final Element element ) {
		final String canonicalName = extractCanonicalName( element );
		return canonicalName.replaceAll( "^(.*)\\.[^.]+", "$1" );
	}

	static String asType( final Element parameter ) {
		return parameter.asType().toString().replaceAll("<[^>]+>","");
	}

	static boolean isAnnotatedWith( final VariableElement parameter, final Class<?extends Annotation> annotationClass ) {
		return parameter.getAnnotation( annotationClass ) != null;
	}

	static String extractPackageName( final String canonicalName ) {
		return canonicalName.replaceAll( "^(.*)\\.[^.]+", "$1" );
	}

	static String extractTypeName( final String canonicalName ) {
		return canonicalName.replaceAll( "^.*\\.([^.]+)", "$1" );
	}

	static String extractReturnTypeFrom( final ExecutableElement method ) {
		final String returnTypeAsString = method.getReturnType().toString();
		if ( "void".equals( returnTypeAsString ) )
			return null;
		return returnTypeAsString.replaceAll("<[^>]+>","");
	}

	static Function<VariableElement, Boolean> isAnnotatedWith( Class<? extends Annotation> annotationClass ) {
		return v -> isAnnotatedWith( v, annotationClass );
	}

	static Function<VariableElement, Boolean> typeIs(Class<?> clazz ) {
		return v -> asType( v ).equals( clazz.getCanonicalName() );
	}
}



