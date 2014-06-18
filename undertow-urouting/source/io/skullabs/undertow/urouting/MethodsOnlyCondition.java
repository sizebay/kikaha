package io.skullabs.undertow.urouting;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import trip.spi.helpers.filter.Condition;

public class MethodsOnlyCondition implements Condition<Element> {

	@Override
	public boolean check(Element object) {
		return object.getKind().equals( ElementKind.METHOD );
	}
}
