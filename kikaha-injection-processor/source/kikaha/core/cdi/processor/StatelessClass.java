package kikaha.core.cdi.processor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.*;
import javax.lang.model.type.*;

import kikaha.core.cdi.helpers.TinyList;
import kikaha.apt.GenerableClass;

public class StatelessClass implements GenerableClass {

	/**
	 * The package where the new class should be placed.
	 */
	final String packageName;

	/**
	 * The exposed type as Canonical Name notation.
	 */
	final String typeCanonicalName;

	/**
	 * The exposed type simple name.
	 */
	final String typeName;
	
	/**
	 * Superclass/Superinterface this class exposes.
	 */
	final String implementations;

	/**
	 * The implementation type as Canonical Name notation.
	 */
	final String implementationCanonicalName;

	/**
	 * Identify if the exposed type is a class or interface.
	 */
	final boolean exposedByClass;

	/**
	 * A list of methods that will be wrapped up.
	 */
	final List<StatelessClassExposedMethod> exposedMethods;

	/**
	 * A list of methods that run after construct the Stateless service.
	 */
	final List<StatelessClassExposedMethod> postConstructMethods;

	/**
	 * A list of methods that run before destroy the Stateless service.
	 */
	final List<StatelessClassExposedMethod> preDestroyMethods;

	/**
	 * @param typeCanonicalName
	 * @param implementationCanonicalName
	 * @param exposedByClass
	 * @param exposedMethods
	 * @param postConstructMethods
	 * @param preDestroyMethods
	 */
	public StatelessClass( final String typeCanonicalName,
	                       final String implementationCanonicalName, final String implementations, final boolean exposedByClass,
	                       final List<StatelessClassExposedMethod> exposedMethods, final List<StatelessClassExposedMethod> postConstructMethods,
	                       final List<StatelessClassExposedMethod> preDestroyMethods ) {
		this.packageName = extractPackageNameFrom( implementationCanonicalName );
		this.typeCanonicalName = typeCanonicalName;
		this.typeName = extractClassNameFrom( typeCanonicalName );
		this.implementationCanonicalName = implementationCanonicalName;
		this.implementations = implementations;
		this.exposedByClass = exposedByClass;
		this.exposedMethods = exposedMethods;
		this.postConstructMethods = postConstructMethods;
		this.preDestroyMethods = preDestroyMethods;
	}

	public int hashCode() {
		return String.format( "%s%s%s%s%s%s",
			packageName, typeCanonicalName,
			typeName, implementationCanonicalName, exposedByClass, exposedMethodsAsString() )
			.hashCode();
	}

	private String exposedMethodsAsString() {
		final StringBuilder buffer = new StringBuilder();
		for ( final StatelessClassExposedMethod method : exposedMethods )
			buffer
					.append( method.name )
					.append( method.returnType )
					.append( method.getParametersWithTypesAsString() );
		return buffer.toString();
	}

	String extractPackageNameFrom( final String canonicalName ) {
		return canonicalName.replaceFirst( "(.*)\\.[^.]+", "$1" );
	}

	String extractClassNameFrom( final String canonicalName ) {
		return canonicalName.replaceFirst( ".*\\.([^.]+)", "$1" );
	}

	public String getPackageName() {
		return packageName;
	}

	public String getTypeCanonicalName() {
		return typeCanonicalName;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getImplementationCanonicalName() {
		return implementationCanonicalName;
	}

	public boolean isExposedByClass() {
		return exposedByClass;
	}

	public List<StatelessClassExposedMethod> getExposedMethods() {
		return exposedMethods;
	}

	public static StatelessClass from( final TypeElement type ) {
		final Set<String> implementationList = SingletonImplementation.getExposedTypes( type );
		final String implementations = 
				implementationList.isEmpty() ? "" 	
				: "{ " + String.join( ",", implementationList) + " }";
		final String typeCanonicalName =  SingletonImplementation.getProvidedServiceClassAsString( type );
		final String implementationCanonicalName = type.asType().toString();
		final boolean exposedByClass = isImplementingClass( typeCanonicalName, type );
		final List<StatelessClassExposedMethod> exposedMethods = retrieveExposedMethods( type );
		return new StatelessClass( typeCanonicalName,
			implementationCanonicalName, implementations, exposedByClass, exposedMethods,
			retrieveMethodsAnnotatedWith( type, javax.annotation.PostConstruct.class ),
			retrieveMethodsAnnotatedWith( type, javax.annotation.PreDestroy.class ) );
	}

	public static boolean isImplementingClass( final String typeCanonicalName, TypeElement type ) {
		while ( !Object.class.getCanonicalName().equals( type.asType().toString() ) ) {
			for ( final TypeMirror interfaceType : type.getInterfaces() )
				if ( typeCanonicalName.contains( interfaceType.toString() ) )
					return false;
			type = (TypeElement)( (DeclaredType)type.getSuperclass() ).asElement();
		}
		return true;
	}

	static List<StatelessClassExposedMethod> retrieveExposedMethods( final TypeElement type ) {
		final List<StatelessClassExposedMethod> list = new TinyList<>();
		for ( final Element method : type.getEnclosedElements() )
			if ( isExposedMethod( method ) )
				list.add( StatelessClassExposedMethod.from( (ExecutableElement)method ) );
		return list;
	}

	@SafeVarargs
	static List<StatelessClassExposedMethod> retrieveMethodsAnnotatedWith( final TypeElement type,
	                                                                       final Class<? extends Annotation>... annotations ) {
		final List<StatelessClassExposedMethod> list = new TinyList<>();
		for ( final Class<? extends Annotation> annotation : annotations )
			for ( final Element method : type.getEnclosedElements() )
				if ( isExposedMethod( method )
					&& method.getAnnotation( annotation ) != null )
					list.add( StatelessClassExposedMethod.from( (ExecutableElement)method ) );
		return list;
	}

	static boolean isExposedMethod( final Element method ) {
		return method.getKind().equals( ElementKind.METHOD )
			&& !isPrivate( (ExecutableElement)method );
	}

	static boolean isPrivate( final ExecutableElement method ) {
		for ( final Modifier modifier : method.getModifiers() )
			if ( modifier.equals( Modifier.PRIVATE ) )
				return true;
		return false;
	}
}
