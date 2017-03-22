package kikaha.apt;

import javax.lang.model.element.*;
import java.util.function.*;
import lombok.*;

/**
 *
 */
@Getter
@RequiredArgsConstructor
public class MethodParametersExtractor {

	final ChainedRules<VariableElement, Function<VariableElement, String>> methodRules;
	final BiFunction<ExecutableElement, VariableElement, String> extractParamFromNonAnnotatedParameter;

	public String extractMethodParamsFrom( final ExecutableElement method ) {
		final StringBuilder buffer = new StringBuilder();
		for ( final VariableElement parameter : method.getParameters() ) {
			if ( buffer.length() > 0 )
				buffer.append( ',' );
			buffer.append( APT.METHOD_PARAM_EOL ).append( extractMethodParamFrom( method, parameter ) );
		}
		return buffer.toString();
	}

	public String extractMethodParamFrom( ExecutableElement method, VariableElement parameter ) {
		for ( final ChainedRules<VariableElement, Function<VariableElement, String>>.Rule rule : methodRules ) {
			if ( rule.matches( parameter ) )
				return rule.result.apply( parameter );
		}
		return extractParamFromNonAnnotatedParameter.apply( method, parameter );
	}
}
