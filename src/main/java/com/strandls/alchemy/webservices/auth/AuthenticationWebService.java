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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.strandls.alchemy.webservices.common.auth.InvalidCredentialsException;

import lombok.RequiredArgsConstructor;

/**
 * Service that other tools can use for authenticating users.
 *
 * @author Ashish Shinde
 *
 */
@Path(AuthenticationWebService.PATH)
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class AuthenticationWebService {
    /**
     * The rest service path.
     */
    public static final String PATH = "auth";
    /**
     * Real authentication service.
     */
    private final AuthenticationService realAuthService;

    /**
     * Authenticate an user.
     *
     * @param username
     *            the user name
     * @param password
     *            the password
     * @return <code>true</code> if the user is authentic, <code>false</code>
     *         otherwise.
     * @throws InvalidCredentialsException
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void authenticate(@FormParam("username") final String username,
            @FormParam("password") final String password) throws InvalidCredentialsException {
        if (!realAuthService.authenticate(username, password)) {
            throw new InvalidCredentialsException(username);
        }
    }

}
