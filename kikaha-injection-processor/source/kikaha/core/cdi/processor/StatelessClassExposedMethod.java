package kikaha.core.cdi.processor;

import static javax.lang.model.type.TypeKind.TYPEVAR;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.util.*;
import kikaha.core.cdi.helpers.TinyList;

public class StatelessClassExposedMethod {

	/**
	 * The method name.
	 */
	final String name;

	/**
	 * The Canonical Name notation of the return type.
	 */
	final String returnType;

	final String generics;

	/**
	 * List of parameter types in Canonical Name notation.
	 */
	final List<String> parameterTypes;

	final List<String> annotations;

	public StatelessClassExposedMethod(
			String name, String returnType,
			List<String> parameterTypes, List<String> annotations, Collection<String> generics ) {
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.annotations = annotations;
		this.generics = generics.isEmpty() ? "" : "<" + String.join( ",", generics ) + ">";
	}

	/**
	 * Identifies if the method should or not returns data.
	 */
	public boolean getReturnable() {
		return !"void".equals( returnType );
	}

	public String getParametersWithTypesAsString() {
		StringBuilder buffer = new StringBuilder();
		int counter = 0;
		for ( String type : parameterTypes ) {
			if ( buffer.length() > 0 )
				buffer.append( ',' );
			buffer.append( type + " arg" + ( counter++ ) );
		}
		return buffer.toString();
	}

	public String getParametersAsString() {
		final StringBuilder buffer = new StringBuilder();
		for ( int counter = 0; counter < parameterTypes.size(); counter++ ) {
			if ( buffer.length() > 0 )
				buffer.append( ',' );
			buffer.append( "arg" + counter );
		}
		return buffer.toString();
	}

	public static StatelessClassExposedMethod from( ExecutableElement method ) {
		final String name = method.getSimpleName().toString();
		final String returnType = method.getReturnType().toString();
		final List<String> parameterTypes = extractParameters( method );
		final List<String> annotations = extractAnnotations( method );
		final Collection<String> generics = extractTypeParameters( method );
		return new StatelessClassExposedMethod( name, returnType, parameterTypes, annotations, generics );
	}

	private static List<String> extractAnnotations( ExecutableElement method ) {
		final List<String> list = new TinyList<>();
		final List<? extends AnnotationMirror> annotationMirrors = method.getAnnotationMirrors();
		for ( final AnnotationMirror annotationMirror : annotationMirrors ) {
			list.add( annotationMirror.getAnnotationType().toString() );
		}
		return list;
	}

	static List<String> extractParameters( ExecutableElement method ) {
		final List<? extends VariableElement> parameters = method.getParameters();
		final List<String> list = new TinyList<>();
		for ( final VariableElement parameter : parameters )
			list.add( parameter.asType().toString() );
		return list;
	}

	static Collection<String> extractTypeParameters( ExecutableElement method ) {
		final Set<String> list = new HashSet<>();
		for ( final VariableElement parameter : method.getParameters() ) {
			if ( parameter.asType() instanceof DeclaredType ) {
				final DeclaredType declaredType = (DeclaredType)parameter.asType();
				for( final TypeMirror mirror : declaredType.getTypeArguments() ) {
					if ( TYPEVAR.equals( mirror.getKind() ) )
						list.add( mirror.toString() );
				}
			}
		}
		return list;
	}
}
