// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SsoSession {
    @JsonProperty("startUrl")
    private String startUrl;

    @JsonProperty("region")
    private String region;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("expiresAt")
    private String expiresAt;

    @JsonProperty("createdAt")
    private String createdAt;

    public final String getStartUrl() {
        return startUrl;
    }

    public final void setStartUrl(final String startUrl) {
        this.startUrl = startUrl;
    }

    public final String getRegion() {
        return region;
    }

    public final void setRegion(final String region) {
        this.region = region;
    }

    public final String getAccessToken() {
        return accessToken;
    }

    public final void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public final String getRefreshToken() {
        return refreshToken;
    }

    public final void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public final String getExpiresAt() {
        return expiresAt;
    }

    public final void setExpiresAt(final String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public final String getCreatedAt() {
        return createdAt;
    }

    public final void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }
}
