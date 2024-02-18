package org.personal.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LambdaConfiguration {
    @JsonProperty
    private String sqsUrl;
    @JsonProperty
    private String awsRegion;

    @JsonProperty
    public String getSqsUrl() {
        return sqsUrl;
    }
    @JsonProperty
    public String getAwsRegion() {
        return awsRegion;
    }
}
