package kikaha.core.cdi.helpers;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

/**
 * Unit tests for {@link TinyList}.
 */
public class TinyListTest {

	@Test
	public void emptyListShouldHaveNoItems(){
		final TinyList<Integer> numbers = new TinyList<>();
		assertEquals( 0, numbers.size() );
		assertTrue( numbers.isEmpty() );
	}

	@Test
	public void cannotIterateEmptyList(){
		int counter = 0;
		for ( Integer i : new TinyList<Integer>() )
			counter+=i;
		assertEquals( 0, counter );
	}

	@Test
	public void addValuesIntoTheList(){
		final TinyList<Integer> numbers = new TinyList<>();
		assertTrue( numbers.add( 1 ) );
		assertFalse( numbers.isEmpty() );
		assertEquals( 1, numbers.size() );
	}

	@Test
	public void retrieveInsertedValueFromTheList(){
		final TinyList<Integer> numbers = new TinyList<>();
		assertTrue( numbers.add( 1 ) );
		assertEquals( 1, numbers.get(0), 0 );
	}

	@Test
	public void retrieveDefinedValueAtTheListConstructor(){
		final TinyList<Integer> numbers = new TinyList<>( 1 );
		assertEquals( 1, numbers.get(0), 0 );
		assertEquals( 1, numbers.size() );
	}

	@Test
	public void iterateOverTheAddedValues(){
		final TinyList<Integer> numbers = new TinyList<>();
		for ( int i=0; i<10; i++ )
			numbers.add( i );

		int counter = 0;
		for ( Integer i : new TinyList<Integer>() )
			assertEquals( counter++, i, 0 );
	}

	@Test
	public void addAllValuesFromCollection(){
		final TinyList<String> data = new TinyList<>();
		data.add( "First" );

		final List<String> extraStrings = Arrays.asList("Second", "Third");
		data.addAll( extraStrings );

		assertEquals( 3, data.size() );
		assertEquals( "First", data.get(0) );
		assertEquals( "Second", data.get(1) );
		assertEquals( "Third", data.get(2) );
	}
}
