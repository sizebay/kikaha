package kikaha.core.cdi.helpers.filter;

import java.util.List;
import kikaha.core.cdi.helpers.TinyList;
import lombok.*;

@RequiredArgsConstructor
public class ChainedCondition<T> implements Condition<T> {

	final List<Condition<T>> conditions = new TinyList<>();

	@Override
	public boolean check( final T object ) {
		for ( val condition : conditions )
			if ( !condition.check( object ) )
				return false;
		return true;
	}

	public void add( final Condition<T> condition ) {
		conditions.add( condition );
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		boolean isFirst = true;
		for ( final Condition<T> c : conditions ){
			if ( !isFirst )
				buffer.append(" AND ");
			buffer.append(c.toString());
			isFirst=false;
		}
		return buffer.toString();
	}
}
