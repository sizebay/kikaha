package kikaha.mustache;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.mustachejava.*;
import com.github.mustachejava.resolver.*;
import kikaha.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

@Singleton
@Getter
@Accessors( fluent = true )
public class MustacheSerializer {

	private MustacheFactory mustacheFactory;
	private boolean shouldCacheTemplates;

	@Inject
	Config config;

	@PostConstruct
	public void readConfiguration() {
		shouldCacheTemplates = config.getBoolean( "server.mustache.cache-templates" );
		mustacheFactory = createMustacheFactory();
	}

	public String serialize( final MustacheTemplate object ) {
		final Writer writer = new StringWriter();
		serialize( object, writer );
		return writer.toString();
	}

	public void serialize( final MustacheTemplate object, final Writer writer ) {
		final String templateName = object.templateName();
		final Mustache compiled = mustacheFactory.compile( formatFileName( templateName ) );
		compiled.execute( writer, object.paramObject() );
	}

	private String formatFileName( final String originalTemplateName ) {
		final String sufix = originalTemplateName.contains( "." )
				? "" : ".mustache";
		final String templateName = originalTemplateName + sufix;
		return templateName;
	}

	private MustacheFactory createMustacheFactory() {
		final String resourcePath = config.getString("server.static.location");
		final File resource = new File(resourcePath);
		final MustacheResolver mustacheResolver = resource.exists()
				? new DefaultResolver( resource )
				: new ClasspathResolver( resourcePath );

		if ( shouldCacheTemplates() )
			return new DefaultMustacheFactory( mustacheResolver );
		return new NotCachedMustacheFactory(mustacheResolver);
	}
}

@RequiredArgsConstructor
class NotCachedMustacheFactory implements MustacheFactory {

	final MustacheResolver mustacheResolver;

	@Delegate
	public MustacheFactory createNewInstance() {
		return new DefaultMustacheFactory( mustacheResolver );
	}
}