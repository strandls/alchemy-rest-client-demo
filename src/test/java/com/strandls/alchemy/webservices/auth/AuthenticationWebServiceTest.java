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

package com.strandls.alchemy.webservices.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;

import com.strandls.alchemy.rest.client.AlchemyRestClientFactory;
import com.strandls.alchemy.webservices.common.auth.InvalidCredentialsException;
import com.strandls.alchemy.webservices.testbase.WebserviceTestBase;

/**
 * Unit tests for {@link AuthenticationWebService}.
 *
 * @author Ashish Shinde
 *
 */
public class AuthenticationWebServiceTest extends WebserviceTestBase {
    /**
     * Client generation factory.
     */
    @Inject
    AlchemyRestClientFactory clientFactory;

    /**
     * Test method for
     * {@link com.strandls.alchemy.webservices.auth.AuthenticationWebService#authenticate(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)}
     * .
     *
     * @throws Exception
     * @throws InvalidCredentialsException
     */
    @Test
    public void testAuthenticate() throws InvalidCredentialsException, Exception {
        clientFactory.getInstance(AuthenticationWebService.class).authenticate("Administrator",
                "0000");
    }

    /**
     * Test method for
     * {@link com.strandls.alchemy.webservices.auth.AuthenticationWebService#authenticate(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)}
     * .
     *
     * @throws Exception
     * @throws InvalidCredentialsException
     */
    @Test
    public void testBadAuthenticate() throws InvalidCredentialsException, Exception {
        final String username = "FakeAdministrator";
        try {
            clientFactory.getInstance(AuthenticationWebService.class)
            .authenticate(username, "0000");

            fail("Should have failed.");
        } catch (final InvalidCredentialsException e) {
            // ensure the exception was correctly serialized.
            assertEquals(username, e.getUsername());
        }
    }

}
