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

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.glassfish.hk2.api.ServiceLocator;
import org.reflections.Reflections;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.strandls.alchemy.webservices.config.AlchemyWebApplication;

/**
 * Test implementation of the web application.
 *
 * @author Ashish Shinde
 *
 */
public class AlchemyTestWebApplication extends AlchemyWebApplication {
    /**
     * Static injector.
     */
    private static Injector injector = null;

    /**
     * @param injector
     *            set the injector to use
     */
    static void setGuiceInjector(final Injector injector) {
        // TODO(ashish) This implementation will not work if tests run in
        // parallel. The H2k and guice bridge code has gotten hacky with statics
        // all over the place. This needs
        // resolution by finding more elegent ways
        AlchemyTestWebApplication.injector = injector;
    }

    /**
     * @param serviceLocator
     */
    @Inject
    public AlchemyTestWebApplication(final ServiceLocator serviceLocator) {
        super(serviceLocator);
    }

    /*
     * (non-Javadoc)
     * @see com.strandls.alchemy.webservices.config.AlchemyWebApplication#
     * getGuiceInjector()
     */
    @Override
    protected Injector getGuiceInjector() {
        return injector;
    }

    /*
     * (non-Javadoc)
     * @see javax.ws.rs.core.Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        final Reflections reflection = new Reflections("com.strandls.alchemy");
        return Sets.filter(reflection.getTypesAnnotatedWith(Path.class), new Predicate<Class<?>>() {
            @Override
            public boolean apply(final Class<?> input) {
                // ignore client stubs
                return !input.getName().toLowerCase().contains("client");
            }
        });
    }
}
