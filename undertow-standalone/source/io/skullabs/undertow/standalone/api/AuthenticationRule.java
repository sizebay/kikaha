package io.skullabs.undertow.standalone.api;

import java.util.List;

public interface AuthenticationRule {

	String pattern();

	List<String> mechanisms();

	List<String> expectedRoles();

}
