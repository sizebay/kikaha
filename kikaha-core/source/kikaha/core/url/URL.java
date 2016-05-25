package kikaha.core.url;

public abstract class URL {

	public static String removeTrailingCharacter( final String original ) {
		int offset = original.length() - 1;
		for ( ; original.charAt( offset ) == '/' && offset > 0; offset-- );
		return original.substring(0, offset + 1);
	}
}
