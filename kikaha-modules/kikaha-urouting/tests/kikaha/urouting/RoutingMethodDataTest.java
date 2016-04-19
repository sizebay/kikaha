package kikaha.urouting;

import static kikaha.urouting.RoutingMethodData.extractPackageName;
import static kikaha.urouting.RoutingMethodData.generateHttpPath;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RoutingMethodDataTest {

	@Test
	public void ensureThatFormatsHttpPathAsExpected() {
		assertThat( generateHttpPath( "", "" ), is( "/" ) );
		assertThat( generateHttpPath( "/", "" ), is( "/" ) );
		assertThat( generateHttpPath( "/hello", "" ), is( "/hello/" ) );
		assertThat( generateHttpPath( "/hello", "/" ), is( "/hello/" ) );
		assertThat( generateHttpPath( "hello", "" ), is( "/hello/" ) );
		assertThat( generateHttpPath( "", "hello" ), is( "/hello/" ) );
		assertThat( generateHttpPath( "hello", "world/{id}" ), is( "/hello/world/{id}/" ) );
	}

	@Test
	public void ensureThatExtractPackageNameAsExpected() {
		String canonicalName = String.class.getCanonicalName();
		String packageName = String.class.getPackage().getName();
		assertThat( extractPackageName( canonicalName ), is( packageName ) );
	}
}
