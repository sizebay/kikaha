package kikaha.mustache;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.StringWriter;

import kikaha.core.api.conf.Configuration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.github.mustachejava.DefaultMustacheFactory;
import com.typesafe.config.Config;

@RunWith( MockitoJUnitRunner.class )
public class MustacheSerializerTest {

	@Provided
	MustacheSerializer serializer;

	@Mock
	Configuration configuration;

	@Mock
	Config config;

	@Test
	@SneakyThrows
	public void ensureThatCompileATemplateWithoutRetrieveFromCache() {
		doReturn( false ).when( config ).getBoolean( eq( "server.mustache.cache-templates" ) );
		provideDependencies();
		val response = readSimulatedResponse();
		val output = new StringWriter();
		serializer.serialize( response.entity(), output );
		assertThat( output.toString(), is( "<h1>Hello Poppins</h1>" ) );
		assertTrue( serializer.mustacheFactory() instanceof NotCachedMustacheFactory );
	}

	@Test
	@SneakyThrows
	public void ensureThatCompileATemplateRetrievingFromCache() {
		doReturn( true ).when( config ).getBoolean( eq( "server.mustache.cache-templates" ) );
		provideDependencies();
		val response = readSimulatedResponse();
		val output = new StringWriter();
		serializer.serialize( response.entity(), output );
		assertThat( output.toString(), is( "<h1>Hello Poppins</h1>" ) );
		assertTrue( serializer.mustacheFactory() instanceof DefaultMustacheFactory );
	}

	MustacheResponse readSimulatedResponse() {
		val user = new User();
		user.setName( "Poppins" );
		val response = MustacheResponse.ok()
			.templateName( "sample" )
			.paramObject( user );
		return response;
	}

	@SneakyThrows
	public void provideDependencies() {
		doReturn( new File( "tests" ).getAbsolutePath() ).when( configuration ).resourcesPath();
		doReturn( config ).when( configuration ).config();

		final ServiceProvider serviceProvider = new DefaultServiceProvider();
		serviceProvider.providerFor( Configuration.class, configuration );
		serviceProvider.provideOn( this );
		serializer = spy( serializer );
	}
}
