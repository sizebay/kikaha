package io.skullabs.undertow.standalone.api;

import java.util.List;

public interface AuthenticationRuleConfiguration {

	String pattern();

	String identityManager();

	String notificationReceiver();

	List<String> mechanisms();

	List<String> expectedRoles();
}
