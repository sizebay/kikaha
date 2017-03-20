package kikaha.protobuf;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.*;
import com.github.mustachejava.*;

public class AnnotatedMethodClassGenerator {

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache providedClazzTemplate;
	final Filer filer;

	public AnnotatedMethodClassGenerator( final Filer filer, final String templateFile ) {
		this.filer = filer;
		this.providedClazzTemplate = this.mustacheFactory.compile( "META-INF/" + templateFile );
	}

	public void generate( final AnnotatedMethodData clazz ) throws IOException {
		final String name = clazz.getPackageName() + "." + clazz.getGeneratedClassName();
		final JavaFileObject sourceFile = filer.createSourceFile( name );
		final Writer writer = sourceFile.openWriter();
		this.providedClazzTemplate.execute( writer, clazz );
		writer.close();
	}
}