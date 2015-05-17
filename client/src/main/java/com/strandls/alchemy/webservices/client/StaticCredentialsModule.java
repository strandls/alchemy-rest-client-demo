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

import javax.inject.Inject;
import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.strandls.alchemy.inject.AlchemyModule;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.webservices.common.auth.Credentials;

/**
 * Provides the credentials from configuration file.
 *
 * @author Ashish Shinde
 *
 */
@AlchemyModule(Environment.Prod)
public class StaticCredentialsModule extends AbstractModule {
    /**
     * The name of the guice parameter for username.
     */
    public static final String USERNAME_PARAM = "com.strandls.alchemy.webservices.client.username";

    /**
     * The name of the guice parameter for password.
     */
    public static final String PASSWORD_PARAM = "com.strandls.alchemy.webservices.client.password";

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
    }

    /**
     * Provider for custom credentials.
     *
     * @param username
     *            the user ID.
     * @param password
     *            the password.
     * @return {@link Credentials} instance constructed using
     *         <code>username</code> and <code>password</code>.
     */
    @Provides
    @Inject
    public Credentials get(@Named(USERNAME_PARAM) final String username,
            @Named(PASSWORD_PARAM) final String password) {
        return Credentials.builder().username(username).password(password).build();
    }
}
