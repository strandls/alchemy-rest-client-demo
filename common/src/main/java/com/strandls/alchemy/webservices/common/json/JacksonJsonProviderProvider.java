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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * {@link JacksonJsonProvider} {@link Provider} for json media binding in
 * jersey.
 *
 * @author Ashish Shinde
 *
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class JacksonJsonProviderProvider implements Provider<JacksonJsonProvider> {
    /**
     * The jackson object mapper.
     */
    private final ObjectMapper mapper;

    /*
     * (non-Javadoc)
     * @see javax.inject.Provider#get()
     */
    @Override
    public JacksonJsonProvider get() {
        return new JacksonJsonProvider(mapper);
    }
}
