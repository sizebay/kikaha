package kikaha.core.url;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class URLMatcher implements Matcher {

	final Iterable<Matcher> patternMatchers;

	public boolean matches( final String string, final Map<String, String> foundParameters ) {
		return matches( new StringCursor( string ), foundParameters );
	}

	@Override
	public boolean matches( final StringCursor string, final Map<String, String> foundParameters ) {
		string.reset();
		for ( final Matcher matcher : patternMatchers )
			if ( !matcher.matches( string, foundParameters ) )
				return false;
		return true;
	}

	public static URLMatcher compile( final String string ) {
		val compiler = new URLPatternCompiler();
		compiler.compile( string );
		return new URLMatcher( compiler.patternMatchers );
	}
}

class URLPatternCompiler {

	final List<Matcher> patternMatchers = new ArrayList<>();
	boolean remainsUnparsedDataInCursor = false;

	public void compile( final String string ) {
		final StringCursor cursor = new StringCursor( string );
		while ( cursor.hasNext() )
			compile( cursor, cursor.next() );
		if ( remainsUnparsedDataInCursor )
			patternMatchers.add( new EqualsMatcher( cursor.substringFromLastMark() ) );
		patternMatchers.add( new EndOfStringMatcher() );
	}

	private void compile( final StringCursor cursor, final char next ) {
		if ( next == '*' || next == '{' ) {
			compileSpecialCharacters( cursor, next );
			remainsUnparsedDataInCursor = false;
		} else
			remainsUnparsedDataInCursor = true;
	}

	private void compileSpecialCharacters( final StringCursor cursor, final char next ) {
		appendEqualsMatcherForBufferedTextUntilNow( cursor );
		cursor.mark();
		handleSpecialCharacter( cursor, next );
		cursor.mark();
	}

	private void appendEqualsMatcherForBufferedTextUntilNow( final StringCursor cursor ) {
		final String token = cursor.substringFromLastMark( 1 );
		if ( !token.isEmpty() )
			patternMatchers.add( new EqualsMatcher( token ) );
	}

	private void handleSpecialCharacter( final StringCursor cursor, final char next ) {
		if ( next == '*' )
			appendMatcherForAsterisk( cursor );
		else if ( next == '{' )
			appendPlaceHolderMatcher( cursor );
	}

	private void appendMatcherForAsterisk( final StringCursor cursor ) {
		if ( cursor.hasNext() )
			patternMatchers.add( new AnyStringNextValidCharMatcher( cursor.next() ) );
		else
			patternMatchers.add( new AnyStringUntilEndMatcher() );
	}

	private void appendPlaceHolderMatcher( final StringCursor cursor ) {
		if ( !cursor.shiftCursorToNextChar( '}' ) )
			throw new RuntimeException( "Invalid expression!" );

		final String placeholder = cursor.substringFromLastMark( 1 );
		if ( cursor.hasNext() ) {
			final char nextChar = cursor.next();
			patternMatchers.add( new PlaceHolderMatcher( placeholder, nextChar ) );
		} else
			patternMatchers.add( new PlaceHolderForAnyStringUntilEndMatcher( placeholder ) );
	}
}
