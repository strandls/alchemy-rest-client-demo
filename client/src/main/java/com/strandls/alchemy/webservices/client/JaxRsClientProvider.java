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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import lombok.RequiredArgsConstructor;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.strandls.alchemy.rest.client.reader.VoidMessageBodyReader;

/**
 * Provides the {@link Client} after applying the jackson bindings.
 *
 * @author Ashish Shinde
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class JaxRsClientProvider implements Provider<Client> {

    /**
     * Setting a large timeout to allow for slow reads.
     */
    private static final int READ_TIMEOUT = 1000000;

    /**
     * The jackson json provider.
     */
    private final JacksonJsonProvider jsonProvider;

    /*
     * (non-Javadoc)
     * @see javax.inject.Provider#get()
     */
    @Override
    @Singleton
    public Client get() {
        final ClientConfig clientConfig = new ClientConfig();

        // register the json provider and other features,
        clientConfig.register(jsonProvider);
        clientConfig.register(MultiPartFeature.class);
        clientConfig.register(VoidMessageBodyReader.class);
        clientConfig.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);

        // AuthRequestBuilderFilter will set the actual credentials just before
        // the web call
        clientConfig.register(HttpAuthenticationFeature.basic("", ""));

        return ClientBuilder.newClient(clientConfig);
    }
}
