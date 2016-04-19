package kikaha.mustache;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import kikaha.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

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
		if ( shouldCacheTemplates() )
			return new DefaultMustacheFactory( new File( resourcePath ) );
		return new NotCachedMustacheFactory( new File( resourcePath ) );
	}
}

@RequiredArgsConstructor
class NotCachedMustacheFactory implements MustacheFactory {

	final File rootDir;

	@Delegate
	public MustacheFactory createNewInstance() {
		return new DefaultMustacheFactory( rootDir );
	}
}