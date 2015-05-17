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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.annotation.WebListener;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.inject.AlchemyModuleLister;

/**
 * A ServletContextListener that creates the global guice injector.
 * The {@link Environment} used to initialize {@link Guice} modules is
 * picked up
 * from a system property {@link #ALCHEMY_WS_ENVIRONMENT_PROPERTY_NAME}. If this
 * property is not set defaults to {@link #ALCHEMY_WS_DEFAULT_ENVIRONMENT}.
 *
 * @author Ashish Shinde
 *
 */
@WebListener
@Singleton
public class AlchemyGuiceServletConfig extends GuiceServletContextListener {
    /**
     * The default environment if none is configured.
     */
    public static final Environment ALCHEMY_WS_DEFAULT_ENVIRONMENT = Environment.Prod;

    /**
     * The {@link Environment} used to locate guice modules.
     */
    public static final String ALCHEMY_WS_ENVIRONMENT_PROPERTY_NAME =
            "com.strandls.alchemy.webservices.config.environment";

    /**
     * Hack to pass on the guice {@link Injector} into
     * {@link AlchemyWebApplication}.
     *
     * TODO(ashish): Correct this once a better way is found.
     */
    private static Injector singletonGuiceInjector;

    /**
     * @return a newly created injector.
     */
    public static Injector createGuiceInjector() {
        final List<Module> modules = new ArrayList<Module>(getAlchemyModules());
        modules.addAll(getMandatoryModules());
        return Guice.createInjector(modules);
    }

    /**
     * @return configured alchemy modules.
     */
    public static Collection<Module> getAlchemyModules() {
        final Environment environment = getEnvironment();
        final Collection<Module> modules = new AlchemyModuleLister().getModules(environment);
        return modules;
    }

    /**
     * @return the {@link Environment} to used for locating modules. Can never
     *         be <code>null</code>.
     */
    private static Environment getEnvironment() {
        return Environment.valueOf(System.getProperty(ALCHEMY_WS_ENVIRONMENT_PROPERTY_NAME,
                ALCHEMY_WS_DEFAULT_ENVIRONMENT.name()));
    }

    /**
     * Create a new injector.
     *
     * @return the created injector.
     */
    public static Injector getGuiceInjector() {
        if (singletonGuiceInjector == null) {
            singletonGuiceInjector = createGuiceInjector();
        }
        return singletonGuiceInjector;
    }

    /**
     * @return mandatory modules to be installed. Can never be <code>null</code>
     *         .
     */
    public static Collection<Module> getMandatoryModules() {
        return Arrays.asList((Module) new AlchemyServletModule(), new AbstractModule() {
            /*
             * (non-Javadoc)
             * @see com.google.inject.AbstractModule#configure()
             */
            @Override
            protected void configure() {
                // bind the environment
                bind(Environment.class).toInstance(getEnvironment());
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
     */
    @Override
    protected Injector getInjector() {
        return getGuiceInjector();
    }
}
