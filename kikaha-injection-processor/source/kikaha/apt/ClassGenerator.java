package kikaha.apt;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.*;
import com.github.mustachejava.*;

public class ClassGenerator {

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache providedClazzTemplate;
	final Filer filer;

	public ClassGenerator( final Filer filer, final String templateFile ) {
		this.filer = filer;
		this.providedClazzTemplate = this.mustacheFactory.compile( "META-INF/" + templateFile );
	}

	public void generate( final GenerableClass clazz ) throws IOException {
		final String name = clazz.getPackageName() + "." + clazz.getGeneratedClassName();
		final JavaFileObject sourceFile = filer.createSourceFile( name );
		final Writer writer = sourceFile.openWriter();
		generateSourceCode( writer, clazz );
		writer.close();
	}

	public String generateSourceCodeOnly( GenerableClass clazz ) {
		final StringWriter writer = new StringWriter();
		generateSourceCode( writer, clazz );
		return writer.toString();
	}

	private void generateSourceCode( Writer writer, GenerableClass clazz ) {
		this.providedClazzTemplate.execute( writer, clazz );
	}
}