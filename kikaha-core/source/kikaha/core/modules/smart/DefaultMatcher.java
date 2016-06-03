package kikaha.core.modules.smart;

import java.util.*;
import io.undertow.server.HttpServerExchange;
import lombok.*;

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

	public static RequestMatcher from( final SmartRouteRule rule )
	{
		val list = new ArrayList<RequestMatcher>();
		list.add( VirtualHostMatcher.from( rule.virtualHost() ) );
		list.add( PathMatcher.from( rule.path() ) );
		return new DefaultMatcher( list );
	}
}
