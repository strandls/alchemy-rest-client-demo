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

/**
 * A simple authentication service.
 *
 * @author Ashish Shinde
 *
 */
public interface AuthenticationService {

    /**
     * Authenticate a user.
     *
     * @param userName
     *            the user name
     * @param password
     *            the user password
     * @return <code>true</code> if the user is authentic, <code>false</code>
     *         otherwise.
     */
    boolean authenticate(final String userName, final String password);
}
