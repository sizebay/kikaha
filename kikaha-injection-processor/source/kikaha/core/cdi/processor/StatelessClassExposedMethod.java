package kikaha.core.cdi.processor;

import javax.lang.model.element.*;
import java.util.List;
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

	/**
	 * List of parameter types in Canonical Name notation.
	 */
	final List<String> parameterTypes;

	final List<String> annotations;

	public StatelessClassExposedMethod( String name, String returnType, List<String> parameterTypes, List<String> annotations ) {
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.annotations = annotations;
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
		return new StatelessClassExposedMethod( name, returnType, parameterTypes, annotations );
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
}
