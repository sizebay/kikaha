package io.skullabs.undertow.standalone.url;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class URLMatcher implements Matcher {

	final Iterable<Matcher> patternMatchers;

	public boolean matches( String string ) {
		return matches( new StringCursor( string ) );
	}

	@Override
	public boolean matches( StringCursor string ) {
		string.reset();
		for ( Matcher matcher : patternMatchers )
			if ( !matcher.matches( string ) )
				return false;
		return true;
	}

	public static URLMatcher compile( String string ) {
		val compiler = new URLPatternCompiler();
		compiler.compile( string );
		return new URLMatcher( compiler.patternMatchers );
	}
}

class URLPatternCompiler {

	final List<Matcher> patternMatchers = new ArrayList<>();
	boolean first = true;

	public void compile( String string ) {
		for ( String token : tokenizeString( string ) )
			patternMatchers.add( newMatcherFor( token ) );
		patternMatchers.add( new EndOfStringMatcher() );
	}

	List<String> tokenizeString( String string ) {
		return new StringSplitter( string ).split( '*' );
	}

	Matcher newMatcherFor( String token ) {
		final char[] tokenAsCharArray = token.toCharArray();
		if ( first ) {
			first = false;
			return new EqualsMatcher( tokenAsCharArray );
		}
		if ( token.isEmpty() )
			return new AnyStringUntilEndMatcher();
		return new EndsWithMatcher( tokenAsCharArray );
	}
}
