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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Throw this exception to return a 401 Unauthorized response. The
 * WWW-Authenticate header is set appropriately and a short message is included
 * in the response entity.
 *
 * @author Ashish Shinde
 */
public class UnauthorizedException extends WebApplicationException {
    /**
     * The serial version ID.
     */
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(final String message, final String realm) {
        super(Response.status(Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"")
                .entity(message).build());
    }
}
