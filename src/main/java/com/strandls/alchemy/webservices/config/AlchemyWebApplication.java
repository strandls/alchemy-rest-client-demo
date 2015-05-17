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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Application;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ServerProperties;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.jvnet.hk2.guice.bridge.api.HK2IntoGuiceBridge;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Application resource for Alchemy that bridges {@link Guice} bi-directionally
 * with HK2 and adds required features.
 *
 * @author Ashish Shinde
 */
public class AlchemyWebApplication extends Application {
    /**
     * Configures service locator.
     *
     * @param serviceLocator
     *            the service locator.
     */
    @Inject
    public AlchemyWebApplication(final ServiceLocator serviceLocator) {
        // create guice hk2 bi directional bridge.
        createBiDirectionalGuiceBridge(serviceLocator, getGuiceInjector());
    }

    /**
     * @return the guice injector to use.
     */
    protected Injector getGuiceInjector() {
        return AlchemyGuiceServletConfig.getGuiceInjector();
    }

    /**
     * Bridge HK2 and Guice injectors.
     *
     * @param serviceLocator
     *            the service locator.
     * @param injector
     *            the guice injector.
     */
    public void createBiDirectionalGuiceBridge(final ServiceLocator serviceLocator,
            final Injector injector) {
        final Injector childInjector =
                injector.createChildInjector(new HK2IntoGuiceBridge(serviceLocator));
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        final GuiceIntoHK2Bridge g2h = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        g2h.bridgeGuiceInjector(childInjector);
    }

    /*
     * (non-Javadoc)
     * @see javax.ws.rs.core.Application#getSingletons()
     */
    @Override
    public Set<Object> getSingletons() {
        final HashSet<Object> singletons = new HashSet<Object>();
        singletons.add(getGuiceInjector().getInstance(JacksonJsonProvider.class));
        return singletons;
    }

    /*
     * (non-Javadoc)
     * @see javax.ws.rs.core.Application#getProperties()
     */
    @Override
    public Map<String, Object> getProperties() {
        // set the injector as a property.
        return ImmutableMap.<String, Object> of(
                ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true);
    }
}
