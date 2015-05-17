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

import java.io.IOException;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.test.AlchemyJerseyTest;
import org.glassfish.jersey.test.spi.TestContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Module;
import com.strandls.alchemy.webservices.common.auth.Credentials;

/**
 * A base class for running webservice tests.
 *
 * @author Ashish Shinde
 *
 */
public class WebserviceTestBase {
    /**
     * Resetable http basic auth filter.
     *
     * @author Ashish Shinde
     */
    private static class ResetableHttpBasicAuthFilter implements ClientRequestFilter {
        /**
         * The auth header value.
         */
        private String authentication = null;
        /**
         * The char set.
         */
        private final Charset characterSet = Charset.forName("iso-8859-1");

        @Override
        public synchronized void filter(final ClientRequestContext rc) throws IOException {
            if (authentication != null) {
                if (!rc.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    rc.getHeaders().add(HttpHeaders.AUTHORIZATION, authentication);
                }
            }
        }

        /**
         * Creates a new HTTP Basic Authentication filter using provided
         * username and password credentials. This constructor allows you to
         * avoid storing plain password value in a String variable.
         *
         * @param username
         *            user name
         * @param password
         *            password
         */
        public synchronized void setCredentials(String username, byte[] password) {
            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = new byte[0];
            }

            final byte[] prefix = (username + ":").getBytes(characterSet);
            final byte[] usernamePassword = new byte[prefix.length + password.length];

            System.arraycopy(prefix, 0, usernamePassword, 0, prefix.length);
            System.arraycopy(password, 0, usernamePassword, prefix.length, password.length);

            authentication = "Basic " + Base64.encodeAsString(usernamePassword);
        }

        /**
         * Unset the credentials.
         */
        public synchronized void unSetCredentials() {
            authentication = null;
        }

    }

    /**
     * The authentication filter.
     */
    @Inject
    private ResetableHttpBasicAuthFilter authFilter;

    /**
     * Setup guice berry.
     */
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(getTestEnvironment());

    /**
     * @return the test environment to use.
     */
    protected Class<? extends Module> getTestEnvironment() {
        return JerseyTestEnv.class;
    }

    @Inject
    protected AlchemyJerseyTest testRunner;

    @Inject
    @ValidUserCredentials
    private Credentials validUserCredentials;

    /**
     * Make a service request.
     *
     * @param path
     *            the service path
     * @param returnType
     *            the return type
     * @return
     */
    protected <T> T request(final String path, final Class<T> returnType) {
        authFilter.unSetCredentials();
        return testRunner.target(path).request().get(returnType);
    }

    /**
     * Set up the test by invoking {@link TestContainer#start() } on
     * the test container obtained from the test container factory.
     *
     * @throws Exception
     *             if an exception is thrown during setting up the test
     *             environment.
     */
    @Before
    public void setUp() throws Exception {
        authFilter.setCredentials(validUserCredentials.getUsername(), validUserCredentials
                .getPassword().getBytes(Charset.defaultCharset()));
        testRunner.getClient().register(authFilter);
    }

    /**
     * Tear down the test by invoking {@link TestContainer#stop() } on
     * the test container obtained from the test container factory.
     *
     * @throws Exception
     *             if an exception is thrown during tearing down the test
     *             environment.
     */
    @After
    public void tearDown() throws Exception {
        testRunner.tearDown();
    }

}
