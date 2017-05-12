package kikaha.cloud.aws.xray;

import org.apache.commons.logging.*;

public class FixedSegmentNamingStrategy implements SegmentNamingStrategy {
    private static final Log logger = LogFactory.getLog(FixedSegmentNamingStrategy.class);
    private String fixedName;

    public FixedSegmentNamingStrategy(String fixedName) {
        this.fixedName = fixedName;
        String overrideName = this.getOverrideName();
        if(null != overrideName) {
            this.fixedName = overrideName;
            if(logger.isInfoEnabled()) {
                logger.info("AWS_XRAY_TRACING_NAME is set, overriding FixedSegmentNamingStrategy constructor argument. Segments generated with this strategy will be named: " + this.fixedName + ".");
            }
        }

    }

    public String nameForRequest(String host) {
        return this.fixedName;
    }
}