package kikaha.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TrailingCharacterReplacementTest {

	static final int MANY_TIMES = 1000000;
	static final String URL_ORIGINAL = "/sample/";
	static final String URL_EXPECTED = "/sample";

	@Test
	public void ensureThatReplaceTrailingCharManuallyInStressedWay() {
		TrailingCharacterStripper stripper = new ManualReplacementStripper();
		assertThatRemovesTrailingCharacterInStressedWay( stripper );
	}

	@Test
	public void ensureThatReplaceTrailingCharByRegExpInStressedWay() {
		TrailingCharacterStripper stripper = new RegexpStripper();
		assertThatRemovesTrailingCharacterInStressedWay( stripper );
	}

	private void assertThatRemovesTrailingCharacterInStressedWay( TrailingCharacterStripper stripper ) {
		for ( int i = 0; i < MANY_TIMES; i++ )
			assertThatRemovesTrailingCharacter( stripper );
	}

	@Test
	public void ensureThatReplaceTrailingCharManually() {
		TrailingCharacterStripper stripper = new ManualReplacementStripper();
		assertThatRemovesTrailingCharacter( stripper );
	}

	@Test
	public void ensureThatReplaceTrailingCharByRegExp() {
		TrailingCharacterStripper stripper = new RegexpStripper();
		assertThatRemovesTrailingCharacter( stripper );
	}

	private void assertThatRemovesTrailingCharacter( TrailingCharacterStripper stripper ) {
		assertThat( stripper.trail( URL_ORIGINAL ), is( URL_EXPECTED ) );
	}
}

interface TrailingCharacterStripper {
	String trail( String original );
}

class RegexpStripper implements TrailingCharacterStripper {

	@Override
	public String trail( String original ) {
		return original.replaceFirst( "/+$", "" );
	}
}

class ManualReplacementStripper implements TrailingCharacterStripper {

	@Override
	public String trail( String original ) {
		final StringBuilder builder = new StringBuilder( original );
		while ( hasRemaningTrailingCharacter( builder ) )
			builder.deleteCharAt( builder.length() - 1 );
		return builder.toString();
	}

	boolean hasRemaningTrailingCharacter( final StringBuilder builder ) {
		return builder != null && builder.length() > 1 && '/' == builder.charAt( builder.length() - 1 );
	}
}
