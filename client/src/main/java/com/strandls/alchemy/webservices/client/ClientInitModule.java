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

import javax.inject.Singleton;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.strandls.alchemy.inject.AlchemyModule;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.rest.client.AlchemyRestClientFactory;
import com.strandls.alchemy.rest.client.request.RequestBuilderFilter;
import com.strandls.alchemy.webservices.common.json.JacksonJsonProviderProvider;

/**
 * Initializes the client to connect to the demo service.
 *
 * @author Ashish Shinde
 *
 */
@AlchemyModule(Environment.All)
public class ClientInitModule extends AbstractModule {
    /**
     * The name of client property for alchemy rest webservices url.
     */
    private static final String ALCHEMY_REST_URL_PROPERTY = "alchemy.rest.url";
    /**
     * The name of client properties file.
     */
    private static final String CLIENT_PROPERTIES = "alchemy-client.properties";
    /**
     * The name of client property for alchemy password.
     */
    private static final String PASSWORD_PROPERTY = "alchemy.rest.password";
    /**
     * The name of client property for alchemy username.
     */
    private static final String USERNAME_PROPERTY = "alchemy.rest.username";

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        final Configuration configuration = getConfiguration();
        bind(String.class)
                .annotatedWith(Names.named(AlchemyRestClientFactory.BASE_URI_NAMED_PARAM))
                .toInstance(configuration.getString(ALCHEMY_REST_URL_PROPERTY));

        // bind jackson json provider
        bind(JacksonJsonProvider.class).toProvider(JacksonJsonProviderProvider.class).in(
                Singleton.class);

        // bind credentials
        if (configuration.getString(USERNAME_PROPERTY) != null) {
            bind(String.class).annotatedWith(Names.named(StaticCredentialsModule.USERNAME_PARAM))
                    .toInstance(configuration.getString(USERNAME_PROPERTY));
        }
        if (configuration.getString(PASSWORD_PROPERTY) != null) {
            bind(String.class).annotatedWith(Names.named(StaticCredentialsModule.PASSWORD_PARAM))
                    .toInstance(configuration.getString(PASSWORD_PROPERTY));
        }

        // install client bindings
        install(new ClientGuiceModule());

        // setup authentication
        bind(RequestBuilderFilter.class).to(AuthRequestBuilderFilter.class);
    }

    /**
     * @return configuration for the client.
     */
    private Configuration getConfiguration() {
        final CompositeConfiguration config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        try {
            config.addConfiguration(new PropertiesConfiguration(CLIENT_PROPERTIES));
        } catch (final ConfigurationException e) {
            throw new RuntimeException(e);
        }
        return config;
    }

}
