package io.skullabs.undertow.standalone.url;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import lombok.val;

import org.junit.Test;

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
		assertThatFitsACompiledPatterForExpectedNumberOfWildcards( compiler, 1 );
	}

	@Test
	public void ensureThatCanCompileTheURLPatternThatMatchesAnyUserRequestIntoTwoMatcherRules() {
		val compiler = new URLPatternCompiler();
		compiler.compile( "/user/*" );
		assertThatHasExpectedSize( compiler, 3 );
		assertFirstMatcherIsEqualsAndLastIsEndOfString( compiler );
		assertThat( compiler.patternMatchers.get( 1 ), is( AnyStringUntilEndMatcher.class ) );
	}

	@Test
	public void ensureThatCanCompileTheURLPatternWithTwoPlaceHoldersIntoThreeMatcherRules() {
		val compiler = new URLPatternCompiler();
		compiler.compile( "/user/*/page/*/" );
		assertThatFitsACompiledPatterForExpectedNumberOfWildcards( compiler, 2 );
	}

	private void assertThatFitsACompiledPatterForExpectedNumberOfWildcards( final URLPatternCompiler compiler, int expectedWildcards ) {
		final int expectedSize = expectedWildcards + 2;
		assertThatHasExpectedSize( compiler, expectedSize );
		assertFirstMatcherIsEqualsAndLastIsEndOfString( compiler );
		assertThatMiddleElementsAreEndsWithMatcher( compiler, expectedSize );
	}

	private void assertThatHasExpectedSize( final io.skullabs.undertow.standalone.url.URLPatternCompiler compiler, int expectedSize ) {
		assertThat( compiler.patternMatchers.size(), is( expectedSize ) );
	}

	private void assertFirstMatcherIsEqualsAndLastIsEndOfString( final URLPatternCompiler compiler ) {
		List<Matcher> patternMatchers = compiler.patternMatchers;
		assertThat( patternMatchers.get( 0 ), is( EqualsMatcher.class ) );
		assertThat( patternMatchers.get( patternMatchers.size() - 1 ), is( EndOfStringMatcher.class ) );
	}

	private void assertThatMiddleElementsAreEndsWithMatcher( final URLPatternCompiler compiler, int expectedSize ) {
		for ( int i = 1; i < expectedSize - 1; i++ )
			assertThat( compiler.patternMatchers.get( i ), is( EndsWithMatcher.class ) );
	}
}
