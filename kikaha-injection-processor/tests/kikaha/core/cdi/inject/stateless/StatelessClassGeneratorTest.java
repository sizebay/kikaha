package kikaha.core.cdi.inject.stateless;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.*;
import kikaha.apt.ClassGenerator;
import kikaha.core.cdi.processor.*;
import org.junit.Test;

public class StatelessClassGeneratorTest {

	final ClassGenerator generator = new ClassGenerator( null, "stateless-class.mustache" );

	@Test
	public void ensureThatGenerateTheExpectedClassFromInterfaceImplementation() throws IOException {
		final StatelessClass statelessClass = createStatelessImplementationOfInterface();
		final String generated = generator.generateSourceCodeOnly( statelessClass );
		final String expected = readFile( "tests-resources/stateless-class-exposing-interface.txt" );
		assertEquals( expected, generated );
	}

	@Test
	public void ensureThatGenerateTheExpectedClassFromExposedServiceByItSelf() throws IOException {
		final StatelessClass statelessClass = createStatelessImplementationOfClass();
		final String generated = generator.generateSourceCodeOnly( statelessClass );
		final String expected = readFile( "tests-resources/stateless-class-exposing-class.txt" );
		assertEquals( expected, generated );
	}

	StatelessClass createStatelessImplementationOfInterface() {
		return new StatelessClass(
				"important.api.Interface", "sample.project.ServiceFromInterface", false,
				list( voidMethod(), returnableMethod() ),
				list( returnableMethod() ),
				list( voidMethod() ) );
	}

	StatelessClass createStatelessImplementationOfClass() {
		return new StatelessClass(
				"sample.project.ServiceFromInterface",
				"sample.project.ServiceFromInterface", true,
				list( voidMethod(), returnableMethod() ),
				list( returnableMethod() ),
				list( voidMethod() ) );
	}

	StatelessClassExposedMethod returnableMethod() {
		return new StatelessClassExposedMethod( "sum", "Long", list( "Double", "Integer" ), list("sample.Annotation") );
	}

	StatelessClassExposedMethod voidMethod() {
		return new StatelessClassExposedMethod( "voidMethod", "void", emptyStringList(), emptyStringList() );
	}

	@SuppressWarnings( "unchecked" )
	<T> List<T> list( final T... ts ) {
		final List<T> list = new ArrayList<T>();
		for ( final T t : ts )
			list.add( t );
		return list;
	}

	List<String> emptyStringList() {
		return new ArrayList<String>();
	}

	String readFile( final String name ) throws IOException {
		final Scanner scanner = new Scanner( new File( name ) );
		try {
			return scanner.useDelimiter( "\\Z" ).next();
		} finally {
			scanner.close();
		}
	}
}
