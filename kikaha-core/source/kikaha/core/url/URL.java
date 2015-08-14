package kikaha.core.url;

public abstract class URL {

	public static String removeTrailingCharacter( final String original ) {
		final StringBuilder builder = new StringBuilder( original );
		while ( hasRemaningTrailingCharacter( builder ) )
			builder.deleteCharAt( builder.length() - 1 );
		return builder.toString();
	}

	public static boolean hasRemaningTrailingCharacter( final StringBuilder builder ) {
		return builder != null && builder.length() > 1 && '/' == builder.charAt( builder.length() - 1 );
	}
}
