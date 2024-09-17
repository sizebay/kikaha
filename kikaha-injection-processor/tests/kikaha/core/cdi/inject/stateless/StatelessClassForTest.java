package kikaha.core.cdi.inject.stateless;

import java.util.List;
import kikaha.core.cdi.Stateless;

@Stateless
public class StatelessClassForTest {

	protected <T>Iterable<T> generic( Class<T> clazz, List<T> obj, Integer integer ) {
		return null;
	}
}