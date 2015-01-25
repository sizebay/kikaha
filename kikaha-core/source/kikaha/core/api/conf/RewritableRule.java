package kikaha.core.api.conf;

public interface RewritableRule {

	String virtualHost();

	String path();

	String target();
}
