package kikaha.urouting.apt;

import static java.lang.String.format;
import static kikaha.apt.APT.asType;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.function.*;
import kikaha.apt.*;
import kikaha.urouting.api.*;

/**
 * @author: miere.teixeira
 */
public class MicroRoutingParameterParser extends MethodParametersExtractor {

	public MicroRoutingParameterParser( BiFunction<ExecutableElement, VariableElement, String> extractParamFromNonAnnotatedParameter ) {
		super( createAnnotationRules(), extractParamFromNonAnnotatedParameter );
	}

	static ChainedRules<VariableElement, Function<VariableElement, String>> createAnnotationRules(){
		final ChainedRules<VariableElement, Function<VariableElement, String>> rules = new ChainedRules<>();
		rules
		   .with( isAnnotatedWith( PathParam.class ), v -> getParam( PathParam.class, v.getAnnotation( PathParam.class ).value(), v ) )
			.and( isAnnotatedWith( QueryParam.class ), v -> getParam( QueryParam.class, v.getAnnotation( QueryParam.class ).value(), v ) )
			.and( isAnnotatedWith( HeaderParam.class ), v -> getParam( HeaderParam.class, v.getAnnotation( HeaderParam.class ).value(), v ) )
			.and( isAnnotatedWith( CookieParam.class ), v -> getParam( CookieParam.class, v.getAnnotation( CookieParam.class ).value(), v ) )
			.and( isAnnotatedWith( FormParam.class ), v -> getParam( FormParam.class, v.getAnnotation( FormParam.class ).value(), v ) )
			.and( isAnnotatedWith( Context.class ), v -> format( "methodDataProvider.getData( exchange, %s.class )", asType( v ) ) );
		return rules;
	}

	static Function<VariableElement, Boolean> isAnnotatedWith( Class<? extends Annotation> annotationClass ) {
		return v -> APT.isAnnotatedWith( v, annotationClass );
	}

	static String getParam( final Class<?> targetAnnotation, final String param, final VariableElement parameter ) {
		final String targetType = asType( parameter );
		return format( "methodDataProvider.get%s( exchange, \"%s\", %s.class )",
				targetAnnotation.getSimpleName(), param, targetType );
	}
}
