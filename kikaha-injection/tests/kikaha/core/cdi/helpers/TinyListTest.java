package kikaha.core.cdi.helpers;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.*;

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

		final List<String> extraStrings = asList("Second", "Third");
		data.addAll( extraStrings );

		assertEquals( 3, data.size() );
		assertEquals( "First", data.get(0) );
		assertEquals( "Second", data.get(1) );
		assertEquals( "Third", data.get(2) );
	}

	@Test @Ignore
	public void stressTest3(){
		final List<Integer> integers = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
		time( "iterator", ()->{
			final Iterator<Integer> iterator = integers.iterator();
			while ( iterator.hasNext() ) {
				iterator.next().toString();
			}
		});
	}

	@Test @Ignore
	public void stressTest1(){
		final List<Integer> integers = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
		time( "toArray", ()->{
			final Integer[] found = integers.toArray(new Integer[integers.size()]);
			int i = 0;
			for (; i < found.length; i++) {
				found[i].toString();
			}
		});
	}

	@Test @Ignore
	public void stressTest4(){
		final List<Integer> integers = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
		time( "randomAccess", ()->{
			int i = 0, s = integers.size();
			for (; i < s; i++) {
				integers.get(i).toString();
			}
		});
	}

	@Test @Ignore
	public void stressTest2(){
		final List<Integer> integers = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
		time( "iterable", ()->{
			for ( Integer found : integers ) {
				found.toString();
			}
		});
	}

	@SneakyThrows
	static void time( String label, Runnable runnable ){
		System.gc();
		Thread.sleep(400);
		long start = System.nanoTime();
		for (int i = 0; i < 1000000; i++)
			runnable.run();
		long end = System.nanoTime();
		System.err.println( label + ": Elapsed: " + ((end - start)/1000000) + "ms" );
	}
}
