package io.skullabs.undertow.standalone.auth;

import io.undertow.security.impl.BasicAuthenticationMechanism;

public class DefaultBasicAuthenticationMechanism extends BasicAuthenticationMechanism {

	public DefaultBasicAuthenticationMechanism() {
		super( "default" );
	}
}
