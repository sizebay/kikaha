package kikaha.core.cdi.helpers.filter;

import javax.lang.model.element.*;
import lombok.val;

public class MethodsOnlyCondition implements Condition<Element> {

	@Override
	public boolean check( final Element object ) {
		val parentClass = object.getEnclosingElement();
		return object.getKind().equals( ElementKind.METHOD )
				&& isNotAbstract( parentClass );
	}

	private boolean isNotAbstract( Element clazz ) {
		for ( val modifier : clazz.getModifiers() )
			if ( Modifier.ABSTRACT.equals( modifier ) )
				return false;
		return true;
	}
}