package kikaha.core.url;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class StringCursor {

	final char[] chars;
	volatile int cursor;

	public StringCursor( String chars ) {
		this( chars.toCharArray() );
	}

	public boolean hasNext() {
		return cursor < chars.length;
	}

	public char next() {
		return chars[cursor++];
	}

	public void reset() {
		cursor = 0;
	}

	boolean matches( StringCursor string ) {
		while ( hasNext() && string.hasNext() )
			if ( next() != string.next() )
				return false;
		return !hasNext();
	}

	boolean shiftCursorToNextChar( char ch ) {
		while ( hasNext() )
			if ( next() == ch )
				return true;
		return false;
	}

	@Override
	public String toString() {
		val buffer = new StringBuilder();
		for ( int i = 0; i < cursor; i++ )
			buffer.append( chars[i] );
		buffer.append( '[' ).append( chars[cursor] ).append( ']' );
		for ( int i = cursor + 1; i < chars.length; i++ )
			buffer.append( chars[i] );
		return buffer.toString();
	}
}
