package kikaha.core.modules.security;

public interface SecurityContext extends io.undertow.security.api.SecurityContext {

	Session getCurrentSession();

	void updateCurrentSession();
}
