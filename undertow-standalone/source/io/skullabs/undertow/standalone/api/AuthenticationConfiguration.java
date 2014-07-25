package io.skullabs.undertow.standalone.api;

import java.util.List;
import java.util.Map;

@SuppressWarnings( "rawtypes" )
public interface AuthenticationConfiguration {

	Map<String, Class> mechisms();

	Class identityManagerClass();

	AuthenticationRule defaultRule();

	List<AuthenticationRule> authenticationRules();
}
