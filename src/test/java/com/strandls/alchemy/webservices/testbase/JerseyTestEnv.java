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

package com.strandls.alchemy.webservices.testbase;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRegistration;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.AlchemyJerseyTest;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.glassfish.jersey.uri.UriComponent;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.guiceberry.GuiceBerryModule;
import com.google.guiceberry.TestScoped;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.inject.AlchemyModuleLister;
import com.strandls.alchemy.rest.client.AlchemyRestClientFactory;
import com.strandls.alchemy.rest.client.RestInterfaceAnalyzer;
import com.strandls.alchemy.rest.client.exception.ResponseToThrowableMapper;
import com.strandls.alchemy.webservices.common.auth.Credentials;
import com.strandls.alchemy.webservices.config.AlchemyGuiceServletConfig;
import com.strandls.alchemy.webservices.config.AlchemyServletModule;

/**
 * Module to set up jersey.
 *
 * @author Ashish Shinde
 *
 */
public class JerseyTestEnv extends AbstractModule {

    /**
     * Context listener that adds modules to the guice berry injector and passes
     * it on to the web application's {@link GuiceFilter}.
     *
     * @author Ashish Shinde
     *
     */
    static class WebserviceTestBaseContextListener extends GuiceServletContextListener {
        private Injector injector;

        /**
         * The parent injector.
         */
        private final Injector parentInjector;

        /**
         * Get a guice injector.
         */
        @Inject
        public WebserviceTestBaseContextListener(final Injector injector) {
            this.parentInjector = injector;
        }

        /**
         * @return create the injector
         */
        private Injector createInjector() {
            // Set environment as testing.
            System.setProperty(AlchemyGuiceServletConfig.ALCHEMY_WS_ENVIRONMENT_PROPERTY_NAME,
                    Environment.Test.name());
            final List<Module> modules = Arrays.asList((Module) new AlchemyServletModule() {
                /*
                 * (non-Javadoc)
                 * @see
                 * com.strandls.alchemy.webservices.config.AlchemyServletModule
                 * #configureServlets()
                 */
                @Override
                protected void configureServlets() {
                    super.configureServlets();
                }

                /*
                 * (non-Javadoc)
                 * @see
                 * com.strandls.alchemy.webservices.config.AlchemyServletModule
                 * #getWebApplicationClass()
                 */
                @Override
                protected Class<? extends Application> getWebApplicationClass() {
                    return AlchemyTestWebApplication.class;
                }
            });
            final Injector finalInjector = parentInjector.createChildInjector(modules);
            AlchemyTestWebApplication.setGuiceInjector(finalInjector);
            return finalInjector;
        }

        /**
         * @return the bridge and properly created injector.
         */
        public Injector getFinalInjector() {
            return getInjector();
        }

        /*
         * (non-Javadoc)
         * @see
         * com.google.inject.servlet.GuiceServletContextListener#getInjector()
         */
        @Override
        protected Injector getInjector() {
            if (injector == null) {
                injector = createInjector();
            }
            return injector;
        }
    }

    /**
     * Get the test container factory.
     *
     * @param contextListener
     * @return
     * @throws TestContainerException
     */
    @Provides
    @Inject
    @TestScoped
    protected AlchemyJerseyTest
    getJerseyTestRunner(final TestContainerFactory testContainerFactory) {
        return new AlchemyJerseyTest(testContainerFactory);
    }

    /**
     * Get the test container factory.
     *
     * @param contextListener
     * @return
     * @throws TestContainerException
     */
    @Provides
    @Inject
    @TestScoped
    protected TestContainerFactory getTestContainerFactory(
            final WebserviceTestBaseContextListener contextListener) throws TestContainerException {
        return new TestContainerFactory() {

            /*
             * Create a test container.
             */
            @Override
            public TestContainer
            create(final URI baseUri, final DeploymentContext deploymentContext) {
                return new TestContainer() {
                    private HttpServer server;

                    @Override
                    public URI getBaseUri() {
                        return baseUri;
                    }

                    @Override
                    public ClientConfig getClientConfig() {
                        return null;
                    }

                    public WebappContext getContext(final URI baseUri) {
                        if (baseUri == null) {
                            throw new IllegalArgumentException("The URI must not be null");
                        }

                        String path = baseUri.getPath();
                        if (path == null) {
                            throw new IllegalArgumentException("The URI path, of the URI "
                                    + baseUri + ", must be non-null");
                        } else if (path.isEmpty()) {
                            throw new IllegalArgumentException("The URI path, of the URI "
                                    + baseUri + ", must be present");
                        } else if (path.charAt(0) != '/') {
                            throw new IllegalArgumentException("The URI path, of the URI "
                                    + baseUri + ". must start with a '/'");
                        }

                        path =
                                String.format("/%s",
                                        UriComponent.decodePath(baseUri.getPath(), true).get(1)
                                        .toString());

                        final WebappContext context = new WebappContext("GrizzlyContext", path);
                        final FilterRegistration filterRegistration =
                                context.addFilter("Guice Filter", GuiceFilter.class);
                        filterRegistration.addMappingForUrlPatterns(
                                EnumSet.of(DispatcherType.REQUEST), "/*");

                        // Initialize and register JSP Servlet
                        final ServletRegistration jspRegistration =
                                context.addServlet("Jersey Servlet Container",
                                        ServletContainer.class);
                        jspRegistration.addMapping("/*");

                        context.addListener(contextListener);
                        return context;
                    }

                    @Override
                    public void start() {
                        try {
                            final WebappContext context = getContext(getBaseUri());
                            server =
                                    GrizzlyHttpServerFactory.createHttpServer(getBaseUri(),
                                            deploymentContext.getResourceConfig());
                            server.getHttpHandler().setAllowEncodedSlash(true);
                            context.setAttribute(
                                    ServerProperties.RESOURCE_VALIDATION_IGNORE_ERRORS, true);
                            context.deploy(server);
                        } catch (final ProcessingException e) {
                            throw new TestContainerException(e);
                        }
                    }

                    @Override
                    public void stop() {
                        this.server.shutdown();
                    }
                };
            }
        };
    }

    /**
     * Jersy client provider to be used by {@link AlchemyRestClientFactory}.
     *
     * @return
     */
    @Provides
    @TestScoped
    @Inject
    public Client getClient(final AlchemyJerseyTest testRunner) {
        // the client in test runner does not get initialized hence this kludge.
        return testRunner.getClient();
    }

    /**
     * Invalid user credentials provider.
     *
     * @param builder
     * @return
     */
    @ValidUserCredentials
    @Provides
    @Singleton
    @Inject
    Credentials getValidUserCredentials() {
        return Credentials.builder().username("Administrator").password("0000").build();
    }

    /**
     * Jersy client provider to be used by {@link AlchemyRestClientFactory}.
     *
     * @return
     */
    @Provides
    @TestScoped
    @Inject
    public AlchemyRestClientFactory getClientFactory(
            @ValidUserCredentials final Credentials credentials,
            final AlchemyJerseyTest testRunner, final Provider<Client> clientProvider,
            final RestInterfaceAnalyzer interfaceAnalyzer, final JacksonJsonProvider jsonProvider,
            final ResponseToThrowableMapper responseToThrowableMapper) {
        final byte[] password = credentials.getPassword().getBytes(Charset.defaultCharset());
        clientProvider.get().register(jsonProvider);

        clientProvider.get().register(
                HttpAuthenticationFeature.universal(credentials.getUsername(), password));
        return new AlchemyRestClientFactory(testRunner.target().getUri().toString() + "rest/",
                clientProvider, interfaceAnalyzer, responseToThrowableMapper, null);
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(Environment.class).toInstance(Environment.Test);

        // install all testing modules.
        for (final Module module : new AlchemyModuleLister().getModules(Environment.Test)) {
            install(module);
        }
        install(new GuiceBerryModule());
    }
}
