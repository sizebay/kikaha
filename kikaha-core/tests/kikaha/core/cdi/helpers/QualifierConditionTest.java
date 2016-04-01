package kikaha.core.cdi.helpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import kikaha.core.cdi.PrintableFoo;
import kikaha.core.cdi.PrintableWorld;
import kikaha.core.cdi.helpers.filter.Condition;
import org.junit.Test;

import kikaha.core.cdi.Foo;
import kikaha.core.cdi.helpers.filter.QualifierCondition;

public class QualifierConditionTest {

	@Test
	public void ensureThatCanRetrieveOnlyQualifiedElements(){
		final Condition<Object> condition = new QualifierCondition<Object>(Arrays.asList(Foo.class));
		assertFalse( condition.check( new PrintableWorld() ) );
		assertTrue( condition.check(new PrintableFoo()) );
	}
}
