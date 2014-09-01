package kikaha.core.url;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class URLMatcherTest {

	static final int MANY_TIMES = 1000000;

	@Test
	public void ensureThatNoWildcardPatternCanMatchExpectedURLs() {
		URLMatcher matcher = URLMatcher.compile( "/panel/admin/" );
		assertTrue( matcher.matches( "/panel/admin/" ) );
		assertFalse( matcher.matches( "/panel/admin/123" ) );
	}

	@Test
	public void ensureThatOneWildcardPatternCanMatchExpectedURLs() {
		URLMatcher matcher = URLMatcher.compile( "/panel/*/" );
		assertTrue( matcher.matches( "/panel/admin/" ) );
		assertFalse( matcher.matches( "/panel/admin/123" ) );
		assertFalse( matcher.matches( "/admin/panel/" ) );
	}

	@Test
	public void ensureThatOneWildcardWithNoEnddingSlashPatternCanMatchExpectedURLs() {
		URLMatcher matcher = URLMatcher.compile( "/panel/*" );
		assertTrue( matcher.matches( "/panel/admin/" ) );
		assertTrue( matcher.matches( "/panel/admin/123" ) );
		assertFalse( matcher.matches( "/admin/panel/" ) );
	}

	@Test
	public void ensureThatTwoWildcardWithNoEnddingSlashPatternCanMatchExpectedURLs() {
		URLMatcher matcher = URLMatcher.compile( "/panel/*/page/*/" );
		assertFalse( matcher.matches( "/panel/admin/" ) );
		assertFalse( matcher.matches( "/panel/admin/123" ) );
		assertFalse( matcher.matches( "/admin/panel/" ) );
		assertTrue( matcher.matches( "/panel/admin/page//" ) );
		assertFalse( matcher.matches( "/panel/admin/page/" ) );
		assertTrue( matcher.matches( "/panel/admin/page/123/" ) );
	}

	@Test( timeout = 2000 )
	public void ensureThatCouldApplyStressTestOnUrlMatchingInAffordableTime() {
		URLMatcher matcher = URLMatcher.compile( "/panel/*/page/*/" );
		for ( int i = 0; i < MANY_TIMES; i++ ) {
			assertFalse( matcher.matches( "/panel/admin/page/" ) );
			assertTrue( matcher.matches( "/panel/admin/page/123/" ) );
		}
	}
}
