package kikaha.urouting;

import static java.lang.String.format;
import static kikaha.apt.APT.*;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import kikaha.apt.GenerableClass;
import kikaha.core.cdi.Stateless;
import kikaha.urouting.api.AsyncResponse;
import kikaha.urouting.api.Consumes;
import kikaha.urouting.api.Context;
import kikaha.urouting.api.CookieParam;
import kikaha.urouting.api.FormParam;
import kikaha.urouting.api.HeaderParam;
import kikaha.urouting.api.MultiPartFormData;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Produces;
import kikaha.urouting.api.QueryParam;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@EqualsAndHashCode( exclude = "identifier" )
@RequiredArgsConstructor
public class RoutingMethodData implements GenerableClass {

	final static String METHOD_PARAM_EOL = "\n\t\t\t";

	final String typeName;
	final String packageName;
	final String methodName;
	final String methodParams;
	final String returnType;
	final String responseContentType;
	final String httpPath;
	final String httpMethod;
	final String serviceInterface;
	final boolean requiresBodyData;
	final boolean requiresFormData;
	final boolean asyncMode;

	@Getter( lazy = true )
	private final long identifier = createIdentifier();

	@Override
	public String toString() {
		return format( "%-70s -> %s:%s ",
			getType() + "." + getMethodName(), getHttpMethod(), getHttpPath() );
	}
}
