package kikaha.core.url;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import lombok.val;

import org.junit.Test;

import tests.AssertThat;

public class URLPatternCompilerTest {

	@Test
	public void ensureThatCanCompileASimpleURLPatternIntoOneMatcherRule() {
		val compiler = new URLPatternCompiler();
		compiler.compile( "/panel/admin/" );
		assertThatFitsACompiledPatterForExpectedNumberOfWildcards( compiler, 0 );
	}

	@Test
	public void ensureThatCanCompileTheUserEndpointWithAsteriskURLPatternIntoTwoMatcherRules() {
		val compiler = new URLPatternCompiler();
		compiler.compile( "/user/*/" );
		assertThatFitsACompiledPatterForExpectedNumberOfWildcards( compiler, 1 );
	}

	@Test
	public void ensureThatCanCompileTheUserCredentialEndpointURLPatternIntoTwoMatcherRules() {
		val compiler = new URLPatternCompiler();
		compiler.compile( "/user/*/credential/" );
		assertThatFitsACompiledPatterForExpectedNumberOfWildcards( compiler, 2 );
	}

	@Test
	public void ensureThatCanCompileTheURLPatternThatMatchesAnyUserRequestIntoTwoMatcherRules() {
		val compiler = new URLPatternCompiler();
		compiler.compile( "/user/*" );
		assertThatHasExpectedSize( compiler, 3 );
		assertFirstMatcherIsEqualsAndLastIsEndOfString( compiler );
		AssertThat.isInstance( compiler.patternMatchers.get( 1 ), AnyStringUntilEndMatcher.class );
	}

	@Test
	public void ensureThatCanCompileTheURLPatternWithTwoPlaceHoldersIntoThreeMatcherRules() {
		val compiler = new URLPatternCompiler();
		compiler.compile( "/user/*/page/*/" );
		assertThatFitsACompiledPatterForExpectedNumberOfWildcards( compiler, 3 );
	}

	private void assertThatFitsACompiledPatterForExpectedNumberOfWildcards( final URLPatternCompiler compiler, final int expectedWildcards ) {
		final int expectedSize = expectedWildcards + 2;
		assertThatHasExpectedSize( compiler, expectedSize );
		assertFirstMatcherIsEqualsAndLastIsEndOfString( compiler );
	}

	private void assertThatHasExpectedSize( final kikaha.core.url.URLPatternCompiler compiler, final int expectedSize ) {
		assertThat( compiler.patternMatchers.size(), is( expectedSize ) );
	}

	private void assertFirstMatcherIsEqualsAndLastIsEndOfString( final URLPatternCompiler compiler ) {
		final List<Matcher> patternMatchers = compiler.patternMatchers;
		AssertThat.isInstance( patternMatchers.get( 0 ), EqualsMatcher.class );
		AssertThat.isInstance( patternMatchers.get( patternMatchers.size() - 1 ), EndOfStringMatcher.class );
	}
}
