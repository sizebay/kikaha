package kikaha.core.url;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class StringCursor {

	final char[] chars;
	volatile int cursor;
	volatile int mark;

	public StringCursor( final String chars ) {
		this( chars.toCharArray() );
	}

	public boolean hasNext() {
		return cursor < chars.length;
	}

	public void end() {
		cursor = chars.length;
	}

	public char next() {
		return chars[cursor++];
	}

	/**
	 * Create a mark positioned at current cursor.
	 */
	public void mark() {
		mark = cursor;
	}

	/**
	 * Create a mark positioned at current cursor. The {@code shiftedCharacters}
	 * argument fixes the gap between the current cursor and the expected
	 * position relative to cursor.
	 *
	 */
	public void mark( final int shiftedCharacters ) {
		mark = cursor + shiftedCharacters;
	}

	public void reset() {
		cursor = 0;
	}

	/**
	 * Reset cursor to same point defined by last mark.
	 */
	public void flip() {
		cursor = mark;
	}

	/**
	 * Check if the following characters in cursor matches the remaining
	 * characters in {@code string} argument.
	 *
	 * @param string
	 * @return
	 */
	boolean matches( final StringCursor string ) {
		while ( hasNext() && string.hasNext() )
			if ( next() != string.next() )
				return false;
		return !hasNext();
	}

	/**
	 * Shift the cursor to the next occurrence of {@code ch} and returns
	 * {@code true}. Otherwise, it will keep the cursor at same position, but
	 * will return {@code false}.
	 *
	 * @param ch
	 * @return
	 */
	boolean shiftCursorToNextChar( final char ch ) {
		mark();
		while ( hasNext() )
			if ( next() == ch )
				return true;
		flip();
		return false;
	}

	/**
	 * Return a new string from last mark until current cursor position.
	 *
	 * @return
	 */
	public String substringFromLastMark() {
		return substringFromLastMark( 0 );
	}

	/**
	 * Return a new string from last mark until current cursor position. The
	 * {@code ignoredLastChars} argument defines which amount of characters
	 * before the current cursor should be ignored.
	 *
	 * @return
	 */
	public String substringFromLastMark( final int ignoredLastChars ) {
		val buffer = new StringBuilder();
		for ( int i = mark; i < cursor - ignoredLastChars; i++ )
			buffer.append( chars[i] );
		return buffer.toString();
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
