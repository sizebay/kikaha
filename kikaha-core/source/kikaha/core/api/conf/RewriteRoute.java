package kikaha.core.api.conf;

public interface RewriteRoute {

	String virtualHost();

	String path();

	String target();
}
