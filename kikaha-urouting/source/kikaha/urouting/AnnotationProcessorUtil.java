package kikaha.urouting;

import java.lang.annotation.Annotation;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import lombok.RequiredArgsConstructor;
import lombok.val;
import trip.spi.Singleton;
import trip.spi.Stateless;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.Filter;

public class AnnotationProcessorUtil {

	final static String METHOD_PARAM_EOL = "\n\t\t\t";

	@SuppressWarnings( "unchecked" )
	public static Iterable<Element> retrieveMethodsAnnotatedWith( final RoundEnvironment roundEnv,
		final Class<? extends Annotation> annotation ) {
		return (Iterable<Element>)Filter.filter(
			roundEnv.getElementsAnnotatedWith( annotation ),
			new MethodsOnlyCondition() );
	}

	public static ExecutableElement retrieveFirstMethodAnnotatedWith( final TypeElement typeElement,
		final Class<? extends Annotation> annotation ) {
		return (ExecutableElement)Filter.first(
			typeElement.getEnclosedElements(),
			new AnnotatedMethodsCondition( annotation ) );
	}

	public static String extractServiceInterfaceFrom( final ExecutableElement method ) {
		final TypeElement classElement = (TypeElement)method.getEnclosingElement();
		return extractServiceInterfaceFrom( classElement );
	}

	public static String extractServiceInterfaceFrom( final TypeElement classElement ) {
		final String canonicalName = getServiceInterfaceProviderClass( classElement ).toString();
		if ( Singleton.class.getCanonicalName().equals( canonicalName )
			|| Stateless.class.getCanonicalName().equals( canonicalName ) )
			return classElement.asType().toString();
		return canonicalName;
	}

	public static TypeMirror getServiceInterfaceProviderClass( final TypeElement service ) {
		try {
			final Singleton singleton = service.getAnnotation( Singleton.class );
			if ( singleton != null )
				singleton.exposedAs();
			final Stateless stateless = service.getAnnotation( Stateless.class );
			if ( stateless != null )
				stateless.exposedAs();
			return service.asType();
		} catch ( final MirroredTypeException cause ) {
			return cause.getTypeMirror();
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

class MethodsOnlyCondition implements Condition<Element> {

	@Override
	public boolean check( final Element object ) {
		val parentClass = object.getEnclosingElement();
		return object.getKind().equals( ElementKind.METHOD )
				&& isNotAbstract( parentClass );
	}

	private boolean isNotAbstract( Element parentClass ) {
		for ( val modifier : parentClass.getModifiers() )
			if ( Modifier.ABSTRACT.equals( modifier ) )
				return false;
		return true;
	}
}

@RequiredArgsConstructor
class AnnotatedMethodsCondition implements Condition<Element> {

	final Class<? extends Annotation> annotationType;

	@Override
	public boolean check( final Element element ) {
		final MethodsOnlyCondition methodsOnlyCondition = new MethodsOnlyCondition();
		return methodsOnlyCondition.check( element )
			&& element.getAnnotation( annotationType ) != null;
	}
}