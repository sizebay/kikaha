package kikaha.core;

import static org.junit.Assert.*;
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
}
