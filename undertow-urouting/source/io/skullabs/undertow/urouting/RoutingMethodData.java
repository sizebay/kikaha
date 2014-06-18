package io.skullabs.undertow.urouting;

import static java.lang.String.format;

import java.lang.annotation.Annotation;

import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import trip.spi.Service;
import urouting.api.*;

public class RoutingMethodData {

	final static String METHOD_PARAM_EOL = "\n\t\t\t";

	final String type;
	final String packageName;
	final String methodName;
	final String methodParams;
	final String returnType;
	final String responseContentType;
	final String httpPath;
	final String httpMethod;
	final String serviceInterface;
	final Long identifier = System.currentTimeMillis();

	public RoutingMethodData( String type, String packageName, String methodName, String methodParams, String returnType,
			String responseContentType, String httpPath, String httpMethod, String serviceInterface ) {
		this.type = type;
		this.packageName = packageName;
		this.methodName = methodName;
		this.methodParams = methodParams;
		this.returnType = returnType;
		this.responseContentType = responseContentType;
		this.httpPath = httpPath;
		this.httpMethod = httpMethod;
		this.serviceInterface = serviceInterface;
	}

	public static RoutingMethodData from(
			ExecutableElement method, Class<? extends Annotation> httpMethodAnnotation ) {
		final String type = method.getEnclosingElement().asType().toString();
		return new RoutingMethodData(
				type, extractPackageName( type ),
				method.getSimpleName().toString(),
				extractMethodParamsFrom( method ),
				extractReturnTypeFrom( method ),
				extractResponseContentTypeFrom( method ),
				measureHttpPathFrom( method ),
				httpMethodAnnotation.getSimpleName(),
				extractServiceInterfaceFrom( method ) );
	}

	public static String extractPackageName( String canonicalName ) {
		return canonicalName.replaceAll( "^(.*)\\.[^\\.]+", "$1" );
	}

	static String extractReturnTypeFrom( ExecutableElement method ) {
		String returnTypeAsString = method.getReturnType().toString();
		if ( "void".equals( returnTypeAsString ) )
			return null;
		return returnTypeAsString;
	}

	static String extractMethodParamsFrom( ExecutableElement method ) {
		final StringBuilder buffer = new StringBuilder().append( METHOD_PARAM_EOL );
		boolean first = true;
		for ( VariableElement parameter : method.getParameters() ) {
			if ( !first )
				buffer.append( ',' );
			buffer.append( extractMethodParamFrom( parameter ) ).append( METHOD_PARAM_EOL );
			first = false;
		}
		return buffer.toString();
	}

	static String extractMethodParamFrom( VariableElement parameter ) {
		String targetType = parameter.asType().toString();
		PathParam pathParam = parameter.getAnnotation( PathParam.class );
		if ( pathParam != null )
			return getParam( PathParam.class, pathParam.value(), targetType );
		QueryParam queryParam = parameter.getAnnotation( QueryParam.class );
		if ( queryParam != null )
			return getParam( QueryParam.class, queryParam.value(), targetType );
		HeaderParam headerParam = parameter.getAnnotation( HeaderParam.class );
		if ( headerParam != null )
			return getParam( HeaderParam.class, headerParam.value(), targetType );
		CookieParam cookieParam = parameter.getAnnotation( CookieParam.class );
		if ( cookieParam != null )
			return getParam( CookieParam.class, cookieParam.value(), targetType );
		Data dataParam = parameter.getAnnotation( Data.class );
		if ( dataParam != null )
			return format( "methodDataProvider.getData( exchange, %s.class )", targetType );
		return format( "methodDataProvider.getBody( exchange, %s.class )", targetType );
	}

	static String getParam( Class<?> targetAnnotation, String param, String targetType ) {
		return format( "methodDataProvider.get%s( exchange, \"%s\", %s.class )",
				targetAnnotation.getSimpleName(), param, targetType );
	}

	static String extractResponseContentTypeFrom( ExecutableElement method ) {
		Produces producesAnnotation = method.getAnnotation( Produces.class );
		if ( producesAnnotation != null )
			producesAnnotation = method.getEnclosingElement().getAnnotation( Produces.class );
		if ( producesAnnotation != null )
			return producesAnnotation.value();
		return null;
	}

	static String measureHttpPathFrom( ExecutableElement method ) {
		final Element classElement = method.getEnclosingElement();
		final Path pathAnnotationOfClass = classElement.getAnnotation( Path.class );
		final String rootPath = pathAnnotationOfClass != null ? pathAnnotationOfClass.value() : "/";
		final Path pathAnnotationOfMethod = method.getAnnotation( Path.class );
		final String methodPath = pathAnnotationOfMethod != null ? pathAnnotationOfMethod.value() : "/";
		return generateHttpPath( rootPath, methodPath );
	}

	public static String generateHttpPath( String rootPath, String methodPath ) {
		return String.format( "/%s/%s/", rootPath, methodPath )
				.replaceAll( "//+", "/" );
	}

	static String extractServiceInterfaceFrom( ExecutableElement method ) {
		TypeElement classElement = (TypeElement)method.getEnclosingElement();
		Service service = classElement.getAnnotation( Service.class );
		if ( service == null )
			return null;
		String canonicalName = getServiceInterfaceProviderClass( service ).toString();
		if ( Service.class.getCanonicalName().equals( canonicalName ) )
			return classElement.asType().toString();
		return canonicalName;
	}

	static TypeMirror getServiceInterfaceProviderClass( Service service ) {
		try {
			service.value();
			return null;
		} catch ( MirroredTypeException cause ) {
			return cause.getTypeMirror();
		}
	}

	public String getPackageName() {
		return packageName;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public String getType() {
		return this.type;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public String getMethodParams() {
		return this.methodParams;
	}

	public String getReturnType() {
		return this.returnType;
	}

	public String getResponseContentType() {
		return this.responseContentType;
	}

	public String getHttpPath() {
		return this.httpPath;
	}

	public String getHttpMethod() {
		return this.httpMethod;
	}
}
