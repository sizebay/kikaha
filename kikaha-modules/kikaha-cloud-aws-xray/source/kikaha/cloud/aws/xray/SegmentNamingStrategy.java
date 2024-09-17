package kikaha.cloud.aws.xray;

import com.amazonaws.xray.entities.StringValidator;

public interface SegmentNamingStrategy {

    String NAME_OVERRIDE_KEY = "AWS_XRAY_TRACING_NAME";

    String nameForRequest(String host);

    default String getOverrideName() {
        String nameOverrideValue = System.getenv(NAME_OVERRIDE_KEY);
        return StringValidator.isNotNullOrBlank(nameOverrideValue)?nameOverrideValue:null;
    }
}