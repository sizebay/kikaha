package kikaha.apt;

import java.lang.annotation.Annotation;
import javax.lang.model.element.Element;
import kikaha.core.cdi.helpers.filter.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnnotatedMethodsCondition implements Condition<Element> {

	final Class<? extends Annotation> annotationType;

	@Override
	public boolean check( final Element element ) {
		final MethodsOnlyCondition methodsOnlyCondition = new MethodsOnlyCondition();
		return methodsOnlyCondition.check( element )
				&& element.getAnnotation( annotationType ) != null;
	}
}