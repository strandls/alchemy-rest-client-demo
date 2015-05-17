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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.servlet.ServletContainer;

import com.google.inject.servlet.ServletModule;
import com.strandls.alchemy.webservices.exception.AlchemyExceptionMapper;

/**
 * Alchemy {@link ServletModule} that sets up filters and servlets.
 *
 * @author Ashish Shinde
 *
 */
public class AlchemyServletModule extends ServletModule {
    /**
     * The prefix for rest API.
     */
    public static final String REST_PATH_PREFIX = "/rest";

    /*
     * (non-Javadoc)
     * @see com.google.inject.servlet.ServletModule#configureServlets()
     */
    @Override
    protected void configureServlets() {
        final Map<String, String> params = new HashMap<String, String>() {
            private static final long serialVersionUID = -7311837596676110198L;
            {
                put("javax.ws.rs.Application", getWebApplicationClass().getCanonicalName());
                put("jersey.config.server.provider.packages", "com.strandls.alchemy");
                put("async-supported", "true");
            }
        };

        // jersey binding
        serve(REST_PATH_PREFIX + "/*").with(ServletContainer.class, params);

        bind(AlchemyExceptionMapper.class);

        // servlet container binding
        bind(ServletContainer.class).in(Singleton.class);
    }

    /**
     * @return the web application class to use.
     */
    protected Class<? extends Application> getWebApplicationClass() {
        return AlchemyWebApplication.class;
    }
}
