package kikaha.core.modules.smart;

import io.undertow.server.HttpServerExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class DefaultMatcher implements RequestMatcher {

	final List<RequestMatcher> listOfMatchers;

	@Override
	public Boolean apply( final HttpServerExchange t, final Map<String, String> u )
	{
		for ( val matcher : listOfMatchers )
			if ( !matcher.apply( t, u ) )
				return false;
		return true;
	}

	public static RequestMatcher from( final RewritableRule rule )
	{
		val list = new ArrayList<RequestMatcher>();
		list.add( VirtualHostMatcher.from( rule.virtualHost() ) );
		list.add( PathMatcher.from( rule.path() ) );
		return new DefaultMatcher( list );
	}
}
