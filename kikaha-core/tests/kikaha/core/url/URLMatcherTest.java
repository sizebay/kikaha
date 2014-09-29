package kikaha.core.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class URLMatcherTest {

	static final int MANY_TIMES = 1000000;

	@Test
	public void ensureThatNoWildcardPatternCanMatchExpectedURLs() {
		final URLMatcher matcher = URLMatcher.compile( "/panel/admin/" );
		assertTrue( matcher.matches( "/panel/admin/", null ) );
		assertFalse( matcher.matches( "/panel/admin/123", null ) );
	}

	@Test
	public void ensureThatOneWildcardPatternCanMatchExpectedURLs() {
		final URLMatcher matcher = URLMatcher.compile( "/panel/*/" );
		assertTrue( matcher.matches( "/panel/admin/", null ) );
		assertFalse( matcher.matches( "/panel/admin/123", null ) );
		assertFalse( matcher.matches( "/admin/panel/", null ) );
	}

	@Test
	public void ensureThatOneWildcardWithNoEnddingSlashPatternCanMatchExpectedURLs() {
		final URLMatcher matcher = URLMatcher.compile( "/panel/*" );
		assertTrue( matcher.matches( "/panel/admin/", null ) );
		assertTrue( matcher.matches( "/panel/admin/123", null ) );
		assertFalse( matcher.matches( "/admin/panel/", null ) );
	}

	@Test
	public void ensureThatTwoWildcardWithNoEnddingSlashPatternCanMatchExpectedURLs() {
		final URLMatcher matcher = URLMatcher.compile( "/panel/*/page/*/" );
		assertFalse( matcher.matches( "/panel/admin/", null ) );
		assertFalse( matcher.matches( "/panel/admin/123", null ) );
		assertFalse( matcher.matches( "/admin/panel/", null ) );
		assertTrue( matcher.matches( "/panel/admin/page//", null ) );
		assertFalse( matcher.matches( "/panel/admin/page/", null ) );
		assertTrue( matcher.matches( "/panel/admin/page/123/", null ) );
	}

	@Test
	public void ensureThatCouldParsePlaceHolders() {
		final Map<String, String> matchedValues = new HashMap<>();
		final URLMatcher matcher = URLMatcher.compile( "/users/{id}/page/*/" );
		assertTrue( matcher.matches( "/users/123/page/10/", matchedValues ) );
		assertEquals( "123", matchedValues.get( "id" ) );
	}

	@Test( timeout = 2000 )
	public void ensureThatCouldApplyStressTestOnUrlMatchingInAffordableTime() {
		final URLMatcher matcher = URLMatcher.compile( "/panel/*/page/*/" );
		for ( int i = 0; i < MANY_TIMES; i++ ) {
			assertFalse( matcher.matches( "/panel/admin/page/", null ) );
			assertTrue( matcher.matches( "/panel/admin/page/123/", null ) );
		}
	}
}
