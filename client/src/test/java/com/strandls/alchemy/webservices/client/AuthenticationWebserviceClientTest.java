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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.inject.AlchemyModuleLister;
import com.strandls.alchemy.webservices.common.auth.InvalidCredentialsException;
import com.strandls.alchemy.webservices.testbase.WebserviceTestBase;

/**
 * Blanket tests for the generated client. Ensures that the rest service proxies
 * work and jackson json serialization with custom implementations is plugged in
 * correctly.
 *
 * @author Ashish Shinde
 *
 */
public class AuthenticationWebserviceClientTest extends WebserviceTestBase {
    /**
     * A separate injector for client code because of possibly conflicting
     * webservice and
     * client bindings.
     */
    private Injector injector;

    @Before
    public void setup() {
        injector = Guice.createInjector(new AlchemyModuleLister().getModules(Environment.Prod));
    }

    /**
     * Test that the generated client works and the json bindings are in place.
     *
     * @throws Exception
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testAuthentication() throws Exception {
        final AuthenticationWebServiceClient authClient =
                injector.getInstance(AuthenticationWebServiceClient.class);
        authClient.authenticate("Administrator", "0000");
    }

    /**
     * Test that the generated client works and the json bindings are in place.
     *
     * @throws Exception
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testAuthenticationFailure() throws Exception {
        final AuthenticationWebServiceClient authClient =
                injector.getInstance(AuthenticationWebServiceClient.class);
        final String username = "FakeAdministrator";
        try {
            authClient.authenticate(username, "");

            fail("Should have failed.");
        } catch (final InvalidCredentialsException e) {
            // ensure the exception was correctly serialized.
            assertEquals(username, e.getUsername());
        }
    }

}
