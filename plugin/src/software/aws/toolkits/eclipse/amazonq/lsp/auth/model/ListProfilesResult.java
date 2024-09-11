// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.auth.model;

import java.util.List;

public class ListProfilesResult {
    private List<Profile> profiles;
    private List<SsoSession> ssoSessions;

    public final List<Profile> getProfiles() {
        return profiles;
    }

    public final void setProfiles(final List<Profile> profiles) {
        this.profiles = profiles;
    }

    public final List<SsoSession> getSsoSessions() {
        return ssoSessions;
    }

    public final void setSsoSessions(final List<SsoSession> ssoSessions) {
        this.ssoSessions = ssoSessions;
    }
}
