package kikaha.core.url;

public abstract class URL {

	public static String removeTrailingCharacter( final String original ) {
		int offset = original.length() - 1;
		for ( ; original.charAt( offset ) == '/'; offset-- );
		return original.substring(0, offset);
	}
}
