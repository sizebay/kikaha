package kikaha.core.url;

import java.util.*;
import kikaha.core.cdi.helpers.*;
import lombok.*;

@RequiredArgsConstructor
public class URLMatcher implements Matcher {

	public static final URLMatcher EMPTY = new URLMatcher( Collections.emptyList() );

	final List<Matcher> patternMatchers;

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
		return compile( string, false );
	}

	public static URLMatcher compile( final String string, final boolean doNotIgnoreSlashes ) {
		val compiler = new URLPatternCompiler( doNotIgnoreSlashes );
		compiler.compile( string );
		return new URLMatcher( compiler.patternMatchers );
	}

	public String replace( final Map<String, String> foundParameters ) {
		val buffer = new StringBuilder();
		replace( buffer, foundParameters );
		return buffer.toString();
	}

	@Override
	public void replace( final StringBuilder buffer, final Map<String, String> foundParameters ) {
		for ( final Matcher matcher : patternMatchers )
			matcher.replace( buffer, foundParameters );
	}

	@Override
	public String toString() {
		val buffer = new StringBuilder();
		for ( final Matcher matcher : patternMatchers )
			buffer.append( matcher.toString() ).append(',');
		return buffer.toString();
	}
}

@RequiredArgsConstructor
class URLPatternCompiler {

	final List<Matcher> patternMatchers = new TinyList<>();
	final boolean doNotIgnoreSlashes;

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
			patternMatchers.add( new PlaceHolderForAnyStringUntilEndMatcher( placeholder, doNotIgnoreSlashes ) );
	}
}
