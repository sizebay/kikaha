package kikaha.protobuf;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static kikaha.protobuf.URoutingAnnotationRules.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import com.google.protobuf.*;
import lombok.*;

/**
 * @author: miere.teixeira
 */
@Getter
@SupportedAnnotationTypes( "protobuf.*" )
public class ProtobufAnnotationProcessor extends AbstractAnnotatedMethodProcessor {

	final List<Class<? extends Annotation>> expectedMethodAnnotations = singletonList( RPC.class );
	final Map<Function<VariableElement, Boolean>, Function<VariableElement, String>> methodRules = URoutingAnnotationRules.createAnnotationRules();
	final String templateName = "protobuf-class.mustache";

	protected void generateMethod(
			final ExecutableElement method, final RoundEnvironment roundEnv,
	        final Class<? extends Annotation> httpMethodAnnotation ) throws IOException
	{
		final ProtobufRPCMethod routingMethodData = toRPCMethod( method );
		info( " " + routingMethodData );
		generator.generate( routingMethodData );
	}

	@NonNull
	private ProtobufRPCMethod toRPCMethod( ExecutableElement method ) throws IOException {
		final String
			type = asType( method.getEnclosingElement() ),
			typeName = extractTypeName( type ),
			packageName = extractPackageName( type ),
			methodName = method.getSimpleName().toString(),
			methodParams = extractMethodParamsFrom( method, this::extractMethodParamFrom ),
			httpPath = format("%s.%s", type, methodName),
			returnType = extractReturnTypeFrom( method );

		ensureReturnTypeIsValid( returnType );

		return new ProtobufRPCMethod( packageName, typeName, methodName, methodParams, returnType, httpPath, "POST",
				methodParams.contains( "asyncResponse" ), methodParams.contains( "parseFrom( bodyData )" ) );
	}

	void ensureReturnTypeIsValid( final String returnType ) throws IOException {
		if ( returnType != null ) {
			final TypeMirror typeToBeChecked = processingEnv.getElementUtils().getTypeElement( returnType ).asType();
			final TypeMirror expectedInterface = processingEnv.getElementUtils().getTypeElement( MessageLite.class.getCanonicalName() ).asType();
			if ( !processingEnv.getTypeUtils().isAssignable( typeToBeChecked, expectedInterface ) )
				throw new IOException( "RPC methods should return Protobuf compatible objects." );
		}
	}

	protected String extractParamFromNonAnnotatedParameter( ExecutableElement method, VariableElement parameter ) {
		return format( "%s.parseFrom( bodyData )", asType( parameter ) );
	}
}
