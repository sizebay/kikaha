package io.skullabs.undertow.standalone.url;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class StringSplitter {

	final static char END_OF_STRING = '§';
	final List<String> strings = new ArrayList<>();
	final StringCursor splittable;
	StringBuilder buffer = new StringBuilder();

	public StringSplitter( String string ) {
		this( new StringCursor( string + END_OF_STRING ) );
	}

	List<String> split( char delimiter ) {
		while ( splittable.hasNext() ) {
			val currentChar = splittable.next();
			if ( currentChar == delimiter || currentChar == END_OF_STRING )
				memorizeCurrentBufferedString();
			else
				memorizeCurrentCharacter( currentChar );
		}
		return strings;
	}

	private StringBuilder memorizeCurrentCharacter( final char currentChar ) {
		return buffer.append( currentChar );
	}

	private void memorizeCurrentBufferedString() {
		strings.add( buffer.toString() );
		buffer = new StringBuilder();
	}
}
