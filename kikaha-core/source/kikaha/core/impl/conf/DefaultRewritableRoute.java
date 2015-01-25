package kikaha.core.impl.conf;

import kikaha.core.api.conf.RewritableRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors( fluent = true )
@NoArgsConstructor
@AllArgsConstructor
public class DefaultRewritableRoute implements RewritableRule {

	String virtualHost;
	String path;
	String target;
}
