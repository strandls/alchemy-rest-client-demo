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

package com.strandls.alchemy.webservices.common.json;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.strandls.alchemy.inject.AlchemyModule;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.reflect.JavaTypeQueryHandler;
import com.strandls.alchemy.rest.client.NotRestInterfaceException;
import com.strandls.alchemy.rest.client.RestInterfaceAnalyzer;
import com.strandls.alchemy.rest.client.exception.ThrowableMaskMixin;
import com.strandls.alchemy.rest.client.exception.ThrowableObjectMapper;

/**
 * Binding for {@link ObjectMapper} used for server side error conversions.
 *
 * @author Ashish Shinde
 *
 */
@Slf4j
@AlchemyModule(Environment.All)
public class ExceptionObjectMapperModule extends AbstractModule {

    /**
     * The jax rs package root.
     */
    private static final String JAVAX_WS_RS_PACKAGE = "javax.ws.rs";
    /**
     * Elixir web service package root.
     */
    private static final String ALCHEMY_SERVICE_PACKAGE = "com.strandls.alchemy";

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
    }

    /**
     * Binding for throwable exception mapper.
     *
     * @param mapper
     * @return
     */
    @Provides
    @Singleton
    @ThrowableObjectMapper
    @Inject
    public ObjectMapper getExceptionObjectMapper(final ObjectMapper mapper,
            final RestInterfaceAnalyzer restInterfaceAnalyzer,
            final JavaTypeQueryHandler typeQueryHandler) {
        // can't copy owing to bug -
        // https://github.com/FasterXML/jackson-databind/issues/245
        final ObjectMapper exceptionMapper = mapper;
        exceptionMapper.registerModule(new SimpleModule() {
            /**
             * The serial version id.
             */
            private static final long serialVersionUID = 1L;

            /*
             * (non-Javadoc)
             * @see
             * com.fasterxml.jackson.databind.module.SimpleModule#setupModule
             * (com.fasterxml.jackson.databind.Module.SetupContext)
             */
            @Override
            public void setupModule(final SetupContext context) {
                // find exceptions thrown by webservices
                final Set<Class<?>> serviceClasses =
                        typeQueryHandler.getTypesAnnotatedWith(ALCHEMY_SERVICE_PACKAGE, Path.class);
                final Set<Class<?>> exceptionsUsed = new HashSet<Class<?>>();
                for (final Class<?> serviceClass : serviceClasses) {
                    // get hold of all rest methods and hence exception
                    try {
                        final Set<Method> restMethods =
                                restInterfaceAnalyzer.analyze(serviceClass).getMethodMetaData()
                                .keySet();
                        for (final Method method : restMethods) {
                            for (final Class<?> exceptionClass : method.getExceptionTypes()) {
                                exceptionsUsed.add(exceptionClass);
                            }
                        }
                    } catch (final NotRestInterfaceException e) {
                        log.error("Error geting exception classes for methods from {}",
                                serviceClass);
                        throw new RuntimeException(e);
                    }
                }

                // add the mixin to all jaxrs classes as well.
                exceptionsUsed.addAll(typeQueryHandler.getSubTypesOf(JAVAX_WS_RS_PACKAGE,
                        WebApplicationException.class));

                for (final Class<?> exceptionClass : exceptionsUsed) {
                    // add a mixin to prevent server stack trace from showing up
                    // to the client.
                    log.debug("Applied mixin mask to {}", exceptionClass);
                    context.setMixInAnnotations(exceptionClass, ThrowableMaskMixin.class);
                }

            }
        });
        exceptionMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return exceptionMapper;
    }

}
