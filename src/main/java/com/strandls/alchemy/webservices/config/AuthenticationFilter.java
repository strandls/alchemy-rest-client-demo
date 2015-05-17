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

package com.strandls.alchemy.webservices.config;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.inject.Key;
import com.strandls.alchemy.webservices.auth.AuthenticationService;
import com.strandls.alchemy.webservices.auth.AuthenticationWebService;
import com.strandls.alchemy.webservices.common.auth.Credentials;

/**
 * This is a filter for a user to restrict him to use web services if he is not
 * authentic. On successful authentication this would set the user credentials
 * in the {@link Request} context for guice injection.
 *
 * @author Ashish Shinde
 *
 */
@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    /**
     * The servlet request.
     */
    @Context
    private HttpServletRequest request;

    /**
     * The authentication service.
     */
    private final AuthenticationService authenticationService;

    /**
     * Decode the basic authentication header and convert it to array of
     * login/password.
     *
     * @param auth
     *            the string encoded authentication
     * @return the login (index 0), the password (index 1), <code>null</code> if
     *         decoding failed.
     */
    private String[] decode(String auth) {
        // Replacing "Basic THE_BASE_64" to "THE_BASE_64" directly
        auth = auth.replaceFirst("[B|b]asic ", "");

        // Decode the Base64 into byte[]
        final byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);

        // If the decode fails in any case
        if (decodedBytes == null || decodedBytes.length == 0) {
            return null;
        }

        // Now we can convert the byte[] into a split array :
        // - the first one is login,
        // - the second one password
        return new String(decodedBytes, Charset.defaultCharset()).split(":", 2);
    }

    /*
     * (non-Javadoc)
     * @see
     * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container
     * .ContainerRequestContext)
     */
    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final String path = requestContext.getUriInfo().getPath();

        if (path.equals(AuthenticationWebService.PATH)) {
            // allow authentication requests without authentication.
            return;
        }

        // Get the authentication passed in HTTP headers parameters
        final String auth = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // If the user does does not provide any HTTP Basic Auth headers
        if (auth == null) {
            log.info("Access denied to anonymous user for {}", path);
            authFailedResponse(requestContext);
            return;

        }

        // lap : loginAndPassword
        final String[] lap = decode(auth);

        // If login or password fail
        if (lap == null || lap.length != 2) {
            log.info("Access denied to anonymous user for {}", path);
            authFailedResponse(requestContext);
            return;
        }

        if (!authenticationService.authenticate(lap[0], lap[1])) {
            log.info("Access denied to  {} for {}", lap[0], path);
            authFailedResponse(requestContext);
            return;
        }

        // authentication is successful
        // inject the credentials to the request context.
        request.setAttribute(Key.get(Credentials.class).toString(),
                Credentials.builder().username(lap[0]).password(lap[1]).build());

        log.debug("Access granted to  {} for {}", lap[0], path);
        return;
    }

    /**
     * @param requestContext
     */
    private void authFailedResponse(final ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Alchemy Webservices authentication required.")
                .header("WWW-Authenticate", "Basic realm=\"Alchemy webservices demo.\"").build());
    }
}
