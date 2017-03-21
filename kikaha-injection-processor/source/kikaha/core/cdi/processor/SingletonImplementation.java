package kikaha.core.cdi.processor;

import javax.enterprise.inject.Typed;
import javax.inject.Qualifier;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.lang.annotation.Annotation;
import java.util.*;
import kikaha.core.cdi.helpers.TinyList;

public class SingletonImplementation {

	public static final List<Class<? extends Annotation>> QUALIFIERS = Arrays.asList( Qualifier.class );

	final String interfaceClass;
	final String implementationClass;

	public SingletonImplementation( final String interfaceClass, final String implementationClass ) {
		this.interfaceClass = NameTransformations.stripGenericsFrom( interfaceClass );
		this.implementationClass = NameTransformations.stripGenericsFrom( implementationClass );
	}

	public String implementationClass() {
		return this.implementationClass;
	}

	public String interfaceClass() {
		return this.interfaceClass;
	}

	public static String getProvidedServiceClassAsString( final TypeElement type ) {
		return getProvidedServiceClassAsString( type, type.asType().toString() );
	}

	private static String getProvidedServiceClassAsString( final TypeElement type, String defaultValue ) {
		final TypeMirror typeMirror = getProvidedServiceClass( type );
		if ( typeMirror != null )
			return typeMirror.toString();
		return defaultValue;
	}

	static TypeMirror getProvidedServiceClass( final TypeElement type ) {
		try {
			final Typed singleton = type.getAnnotation( Typed.class );
			if ( singleton != null ) {
				final Class<?> clazz = singleton.value()[0];
				clazz.getCanonicalName();
			}
			return null;
		} catch ( final MirroredTypesException cause ) {
			return cause.getTypeMirrors().get( 0 );
		} catch ( final java.lang.ClassCastException cause ) {
			System.err.println( cause.getMessage() );
			return null;
		}
	}

	public static List<String> getQualifierAnnotation( final Element type ) {
		final List<String> qualifierAnn = new TinyList<>();
		for ( final AnnotationMirror annotationMirror : type.getAnnotationMirrors() )
			for ( final Class<? extends Annotation> annClass : QUALIFIERS )
				if ( isAnnotationPresent( annotationMirror.getAnnotationType().asElement(), annClass.getCanonicalName() ) )
					qualifierAnn.add( annotationMirror.getAnnotationType().toString() );
		return qualifierAnn;
	}

	static boolean isAnnotationPresent( final Element element, String canonicalName ) {
		for ( final AnnotationMirror annotationMirror : element.getAnnotationMirrors() ) {
			final DeclaredType annotationType = annotationMirror.getAnnotationType();
			final TypeElement annotationElement = (TypeElement)annotationType.asElement();
			if ( annotationElement.getQualifiedName().contentEquals( canonicalName ) )
				return true;
		}
		return false;
	}
}
