package kikaha.core.cdi.helpers.filter;

import java.lang.annotation.Annotation;
import java.util.*;
import javax.annotation.processing.RoundEnvironment;
import javax.enterprise.inject.Typed;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

public class AnnotationProcessorUtil {

	final static String METHOD_PARAM_EOL = "\n\t\t\t";

	@SuppressWarnings( "unchecked" )
	public static List<ExecutableElement> retrieveMethodsAnnotatedWith( final RoundEnvironment roundEnv,
			final Class<? extends Annotation> annotation )
	{
		return (List) asList((Iterable<Element>) Filter.filter(
				roundEnv.getElementsAnnotatedWith( annotation ),
				new MethodsOnlyCondition() ));
	}

	private static <T> List<T> asList( Iterable<T> items ){
		final List<T> list = new ArrayList<>();
		for ( final T item : items )
			list.add(item);
		return list;
	}

	public static ExecutableElement retrieveFirstMethodAnnotatedWith( final TypeElement typeElement,
			final Class<? extends Annotation> annotation )
	{
		return (ExecutableElement)Filter.first(
				typeElement.getEnclosedElements(),
				new AnnotatedMethodsCondition( annotation ) );
	}

	public static String extractServiceInterfaceFrom( final ExecutableElement method ) {
		final TypeElement classElement = (TypeElement)method.getEnclosingElement();
		final String clazzName = extractServiceInterfaceFrom(classElement);
		if ( clazzName != null )
			return clazzName;
		return classElement.asType().toString();
	}

	public static String extractServiceInterfaceFrom( final TypeElement classElement ) {
		final Typed typedAnn = classElement.getAnnotation(Typed.class);
		try {
			if ( typedAnn != null ) {
				final Class<?>[] classes = typedAnn.value();
				classes[0].getCanonicalName();
				return null;
			}
			return classElement.asType().toString();
		} catch ( MirroredTypesException cause ) {
			final TypeMirror typeMirror = cause.getTypeMirrors().get(0);
			return typeMirror.toString();
		}
	}

	public static String extractCanonicalName( final Element element ) {
		return element.asType().toString();
	}

	public static String extractPackageName( final Element element ) {
		final String canonicalName = extractCanonicalName( element );
		return canonicalName.replaceAll( "^(.*)\\.[^\\.]+", "$1" );
	}

	public static String extractMethodParamsFrom( final ExecutableElement method, final MethodParameterParser parser ) {
		final StringBuilder buffer = new StringBuilder().append( METHOD_PARAM_EOL );
		boolean first = true;
		for ( final VariableElement parameter : method.getParameters() ) {
			if ( !first )
				buffer.append( ',' );
			buffer.append( parser.parse( method, parameter ) ).append( METHOD_PARAM_EOL );
			first = false;
		}
		return buffer.toString();
	}

	public interface MethodParameterParser {
		String parse(
				final ExecutableElement method,
				final VariableElement variable );
	}
}



