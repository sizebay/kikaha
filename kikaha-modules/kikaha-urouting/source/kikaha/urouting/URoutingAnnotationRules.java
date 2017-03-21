package kikaha.urouting;

import static java.lang.String.format;
import static kikaha.apt.APT.asType;
import static kikaha.apt.APT.isAnnotatedWith;

import javax.lang.model.element.*;
import java.util.*;
import java.util.function.Function;
import kikaha.urouting.api.*;

/**
 * @author: miere.teixeira
 */
public interface URoutingAnnotationRules {

	static Map<Function<VariableElement, Boolean>, Function<VariableElement, String>> createAnnotationRules(){
		Map< Function<VariableElement, Boolean>, Function<VariableElement, String> > rules = new HashMap<>();
		rules.put( v -> isAnnotatedWith( v, PathParam.class ), v -> getParam( PathParam.class, v.getAnnotation( PathParam.class ).value(), v ) );
		rules.put( v -> isAnnotatedWith( v, QueryParam.class ), v -> getParam( QueryParam.class, v.getAnnotation( QueryParam.class ).value(), v ) );
		rules.put( v -> isAnnotatedWith( v, HeaderParam.class ), v -> getParam( HeaderParam.class, v.getAnnotation( HeaderParam.class ).value(), v ) );
		rules.put( v -> isAnnotatedWith( v, CookieParam.class ), v -> getParam( CookieParam.class, v.getAnnotation( CookieParam.class ).value(), v ) );
		rules.put( v -> isAnnotatedWith( v, FormParam.class ), v -> getParam( FormParam.class, v.getAnnotation( FormParam.class ).value(), v ) );
		rules.put( v -> isAnnotatedWith( v, Context.class ), v -> format( "methodDataProvider.getData( exchange, %s.class )", asType( v ) ) );
		return rules;
	}

	static String getParam( final Class<?> targetAnnotation, final String param, final VariableElement parameter ) {
		final String targetType = asType( parameter );
		return format( "methodDataProvider.get%s( exchange, \"%s\", %s.class )",
				targetAnnotation.getSimpleName(), param, targetType );
	}
}
