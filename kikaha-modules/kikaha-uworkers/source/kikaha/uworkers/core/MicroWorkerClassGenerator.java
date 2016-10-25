package kikaha.uworkers.core;

import java.io.*;
import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import com.github.mustachejava.*;
import lombok.RequiredArgsConstructor;

/**
 * Generator source code for Worker listeners.
 */
@RequiredArgsConstructor
public class MicroWorkerClassGenerator {

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache providedClazzTemplate;
	final Filer filer;

	public MicroWorkerClassGenerator( final Filer filer ) {
		this.filer = filer;
		this.providedClazzTemplate = this.mustacheFactory.compile( "META-INF/worker-listener-class.mustache" );
	}

	public void generate( final MicroWorkerListenerClass clazz ) throws IOException {
		final String name = createClassCanonicalName( clazz );
		final JavaFileObject sourceFile = filer.createSourceFile( name );
		final Writer writer = sourceFile.openWriter();
		this.providedClazzTemplate.execute( writer, clazz );
		writer.close();
	}

	String createClassCanonicalName( final MicroWorkerListenerClass clazz ) {
		return clazz.getPackageName() + "." + clazz.getClassName();
	}
}
