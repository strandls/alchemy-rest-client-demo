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

package org.glassfish.jersey.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;

import lombok.Getter;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.reflections.Reflections;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.strandls.alchemy.rest.client.reader.VoidMessageBodyReader;

/**
 * A {@link JerseyTest} runner that will not be the base class for tests but
 * used as an instance in tests.
 *
 * @author Ashish Shinde
 *
 */
public class AlchemyJerseyTest extends JerseyTest {
    /**
     * The test container factory.
     */
    private final TestContainerFactory testContainerFactory;

    @Getter(lazy = true, onMethod = @_({ @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value = { "JLM_JSR166_UTILCONCURRENT_MONITORENTER",
                    "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE" },
            justification = "Findbugs warnings on lombok generated code not critical.") }))
    private final Client client = createClient();

    /**
     * @param testContainerFactory
     */
    public AlchemyJerseyTest(final TestContainerFactory testContainerFactory) {
        super(testContainerFactory);
        this.testContainerFactory = testContainerFactory;
        try {
            setUp();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected Application configure() {
        final Reflections reflection = new Reflections("com.strandls.alchemy");
        final Set<Class<?>> webservices =
                Sets.filter(reflection.getTypesAnnotatedWith(Path.class),
                        new Predicate<Class<?>>() {
                    @Override
                    public boolean apply(final Class<?> input) {
                        // ignore client stubs
                        return !input.getName().toLowerCase().contains("client");
                    }
                });
        final ResourceConfig config = new ResourceConfig(webservices);
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(ServerProperties.RESOURCE_VALIDATION_IGNORE_ERRORS, Boolean.TRUE);
        config.setProperties(properties);
        return config;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.glassfish.jersey.test.JerseyTest#getTestContainerFactory()
     */
    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return testContainerFactory;
    }

    /**
     * @return the jersey client to use.
     */
    private Client createClient() {
        return getClient(getTestContainer().getClientConfig());
    }

    /**
     * Create an instance of test {@link Client} using the client configuration
     * provided by the configured
     * {@link org.glassfish.jersey.test.spi.TestContainer}.
     * <p>
     * If the {@code TestContainer} does not provide any client configuration
     * (passed {@code clientConfig} is {@code null}), the default implementation
     * of this method first creates an empty new
     * {@link org.glassfish.jersey.client.ClientConfig} instance. The client
     * configuration (provided by test container or created) is then passed to
     * {@link #configureClient(org.glassfish.jersey.client.ClientConfig)} which
     * can be overridden in the {@code JerseyTest} subclass to provide custom
     * client configuration. At last, new JAX-RS {@link Client} instance is
     * created based on the resulting client configuration.
     * </p>
     *
     * @param clientConfig
     *            test client default configuration. May be {@code null}.
     * @return A Client instance.
     */
    private Client getClient(ClientConfig clientConfig) {
        if (clientConfig == null) {
            clientConfig = new ClientConfig();
        }

        clientConfig.register(VoidMessageBodyReader.class);

        configureClient(clientConfig);

        return ClientBuilder.newClient(clientConfig);
    }

}
