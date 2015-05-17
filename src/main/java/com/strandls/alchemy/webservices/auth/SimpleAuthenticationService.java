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

import javax.inject.Singleton;

import org.apache.commons.lang3.ObjectUtils;

/**
 * A dummy authentication service that responds to username "Administrator" and
 * password "0000".
 *
 * @author Ashish Shinde
 *
 */
@Singleton
public class SimpleAuthenticationService implements AuthenticationService {

    /*
     * (non-Javadoc)
     * @see
     * com.strandls.alchemy.webservices.auth.AuthenticationService#authenticate
     * (java.lang.String, java.lang.String)
     */
    @Override
    public boolean authenticate(final String userName, final String password) {
        return ObjectUtils.equals(userName, "Administrator")
                && ObjectUtils.equals(password, "0000");
    }

}
