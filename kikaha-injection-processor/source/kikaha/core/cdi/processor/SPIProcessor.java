package kikaha.core.cdi.processor;

import javax.annotation.processing.*;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.swing.plaf.nimbus.State;
import javax.tools.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.file.Files;
import java.util.*;
import kikaha.apt.*;
import kikaha.core.cdi.Stateless;
import kikaha.core.cdi.helpers.ServiceLoader;

@SupportedAnnotationTypes( { "javax.inject.*", "kikaha.core.cdi.*" } )
public class SPIProcessor extends AnnotationProcessor {

	static final String EOL = "\n";
	static final String SERVICES = "META-INF/services/";

	final Map<String, Set<String>> singletons = new HashMap<String, Set<String>>();
	ClassGenerator producerGenerator;
	ClassGenerator statelessGenerator;

	@Override
	public synchronized void init( ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		producerGenerator = new ClassGenerator( processingEnv.getFiler(), "provided-class.mustache" );
		statelessGenerator = new ClassGenerator( processingEnv.getFiler(), "stateless-class.mustache" );
	}

	@Override
	public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv ) {
		try {
			if ( !roundEnv.processingOver() )
				process( roundEnv );
			else
				flush();
		} catch ( final IOException e ) {
			e.printStackTrace();
		}
		return false;
	}

	protected void process( final RoundEnvironment roundEnv ) throws IOException {
		processSingletons( roundEnv, Singleton.class );
		processStateless( roundEnv, Stateless.class );
		processProducers( roundEnv, Produces.class );
	}

	public void processStateless( final RoundEnvironment roundEnv, Class<? extends Annotation> ann ) throws IOException {
		if ( ann != null ) {
			final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( ann );
			for ( final Element element : annotatedElements )
				if ( element.getKind() == ElementKind.CLASS )
					memorizeAServiceImplementation( StatelessClass.from( (TypeElement)element ) );
		}
	}

	void memorizeAServiceImplementation( final StatelessClass clazz ) throws IOException {
		createAStatelessClassFrom( clazz );
		final String interfaceClass = clazz.getTypeCanonicalName();
		final String implementationClass = clazz.getGeneratedClassCanonicalName();
		memorizeAServiceImplementation( interfaceClass, implementationClass );
	}

	void createAStatelessClassFrom( final StatelessClass clazz ) throws IOException {
		final String name = clazz.getGeneratedClassCanonicalName();
		if ( !classExists( name ) ) {
			info( "  > Generating " + name );
			statelessGenerator.generate( clazz );
		}
	}

	public void processSingletons( final RoundEnvironment roundEnv, Class<? extends Annotation> ann ) throws IOException {
		if ( ann != null ) {
			final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( ann );
			for ( final Element element : annotatedElements )
				if ( element.getKind() == ElementKind.CLASS )
					memorizeAllImplementations( (TypeElement)element );
		}
	}

	private void memorizeAllImplementations( final TypeElement type ) throws IOException {
		final String implementationClass = type.asType().toString();
		final TypeMirror superinterfaceOrClass = SingletonImplementation.getProvidedServiceClass( type );
		if ( superinterfaceOrClass != null )
			memorizeAServiceImplementation( new SingletonImplementation( superinterfaceOrClass.toString(), implementationClass ) );
		else
			for ( final TypeMirror interfaceType : type.getInterfaces() )
				memorizeAServiceImplementation( new SingletonImplementation( interfaceType.toString(), implementationClass ) );
		memorizeAServiceImplementation( new SingletonImplementation( implementationClass, implementationClass ) );
	}

	void memorizeAServiceImplementation( final SingletonImplementation from ) {
		final String interfaceClass = from.interfaceClass();
		final String implementationClass = from.implementationClass();
		memorizeAServiceImplementation( interfaceClass, implementationClass );
	}

	void memorizeAServiceImplementation( final String interfaceClass, final String implementationClass ) {
		Set<String> list = this.singletons.get( interfaceClass );
		if ( list == null ) {
			list = readAListWithAllCreatedClassesImplementing( interfaceClass );
			this.singletons.put( interfaceClass, list );
		}
		list.add( implementationClass );
	}

	private HashSet<String> readAListWithAllCreatedClassesImplementing( final String interfaceClass ) {
		final LinkedHashSet<String> foundSingletons = new LinkedHashSet<String>();
		for ( final Class<?> implementationClass : ServiceLoader.loadImplementationsFor( interfaceClass ) )
			foundSingletons.add( implementationClass.getCanonicalName() );
		return foundSingletons;
	}

	public void processProducers( final RoundEnvironment roundEnv, Class<? extends Annotation> ann ) throws IOException {
		if ( ann != null ) {
			final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( ann );
			for ( final Element element : annotatedElements )
				if ( element.getKind() == ElementKind.METHOD ) {
					if ( !isStatelessClass( element.getEnclosingElement() ) )
						createAProducerFrom( ProducerClass.from( (ExecutableElement) element ) );
				}
		}
	}

	private boolean isStatelessClass( Element enclosingElement ) {
		return enclosingElement.getAnnotation( Stateless.class ) != null;
	}

	void createAProducerFrom( final GenerableClass clazz ) throws IOException {
		final String name = clazz.getGeneratedClassCanonicalName();
		if ( !classExists( name ) ) {
			info( "  > Generating " + name );
			producerGenerator.generate( clazz );
		}
	}

	boolean classExists( final String name ) {
		try {
			Class.forName( name );
			return true;
		} catch ( final Exception cause ) {
			return false;
		}
	}

	void createSingletonMetaInf() throws IOException {
		info( "Running dependency injection optimization..." );
		for ( final String interfaceClass : this.singletons.keySet() ) {
			final Set<String> implementations = readResourceIfExists( SERVICES + interfaceClass );
			implementations.addAll( this.singletons.get(interfaceClass) );
			try ( final Writer resource = createResource( SERVICES + interfaceClass ) ) {
				debug("  > " + interfaceClass);
				for (final String implementation : implementations)
					resource.write(implementation + EOL);
			}
		}
	}

	Set<String> readResourceIfExists( final String resourcePath ) throws IOException {
		final Set<String> resourceContent = new HashSet<>();
		final FileObject resource = processingEnv.getFiler().getResource( StandardLocation.CLASS_OUTPUT, "", resourcePath );
		final File file = new File( resource.toUri() );
		if ( file.exists() )
			resourceContent.addAll( Files.readAllLines( file.toPath() ) );
		return resourceContent;
	}

	Writer createResource( final String resourcePath ) throws IOException {
		final FileObject resource = processingEnv.getFiler().getResource( StandardLocation.CLASS_OUTPUT, "", resourcePath );
		final URI uri = resource.toUri();
		createNeededDirectoriesTo( uri );
		final File file = createFile( uri );
		return new FileWriter( file );
	}

	void createNeededDirectoriesTo( final URI uri ) {
		File dir = null;
		if ( uri.isAbsolute() )
			dir = new File( uri ).getParentFile();
		else
			dir = new File( uri.toString() ).getParentFile();
		dir.mkdirs();
	}

	File createFile( final URI uri ) throws IOException {
		final File file = new File( uri );
		if ( !file.exists() )
			file.createNewFile();
		return file;
	}

	public void flush() throws IOException {
		if ( !this.singletons.isEmpty() )
			createSingletonMetaInf();
	}
}
