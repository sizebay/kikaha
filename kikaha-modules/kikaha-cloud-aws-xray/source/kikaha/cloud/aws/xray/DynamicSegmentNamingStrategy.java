package kikaha.cloud.aws.xray;

import com.amazonaws.xray.entities.SearchPattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicSegmentNamingStrategy implements SegmentNamingStrategy {

    private String recognizedHosts;
    private String fallbackName;

    public DynamicSegmentNamingStrategy(String fallbackName) {
        this(fallbackName, "*");
    }

    public DynamicSegmentNamingStrategy(String fallbackName, String recognizedHosts) {
        this.fallbackName = fallbackName;

        final String overrideName = this.getOverrideName();
        if(null != overrideName) {
            this.fallbackName = this.getOverrideName();
            log.info("AWS_XRAY_TRACING_NAME is set, overriding DynamicSegmentNamingStrategy constructor argument. Segments generated with this strategy will be named: " + this.fallbackName + " when the host header is unavilable or does not match the provided recognizedHosts pattern.");
        }

        this.recognizedHosts = recognizedHosts;
    }

    public String nameForRequest(String host) {
        return host == null && null == this.recognizedHosts ||
            !SearchPattern.wildcardMatch(this.recognizedHosts, host)
                ? this.fallbackName
                : host;
    }
}
