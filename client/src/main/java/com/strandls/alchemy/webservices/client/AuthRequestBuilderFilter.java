/*
 * Copyright (C) 2015 Strand Life Sciences.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strandls.alchemy.webservices.client;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.client.Invocation.Builder;

import lombok.RequiredArgsConstructor;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.strandls.alchemy.rest.client.request.RequestBuilderFilter;
import com.strandls.alchemy.webservices.common.auth.Credentials;

/**
 * Request builder that sets autntication credentials.
 *
 * @author Ashish Shinde
 *
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class AuthRequestBuilderFilter implements RequestBuilderFilter {
    /**
     * The credentials provider.
     */
    private final Provider<Credentials> credentialsProvider;

    /*
     * (non-Javadoc)
     * @see
     * com.strandls.alchemy.rest.client.request.RequestBuilderFilter#apply(javax
     * .ws.rs.client.Invocation.Builder)
     */
    @Override
    public void apply(final Builder builder) {
        final Credentials credentials = credentialsProvider.get();
        String user = null;
        String password = null;

        if (credentials instanceof NullCredentials) {
            user = "";
            password = "";
        } else {
            user = credentials.getUsername();
            password = credentials.getPassword();
        }
        builder.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, user)
        .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, password);
    }
}
