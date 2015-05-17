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

package com.strandls.alchemy.webservices.exception;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.strandls.alchemy.rest.client.exception.ThrowableToResponseMapper;

/**
 * Mapper for {@link Exception}s generated from the webservices.
 *
 * @author Ashish Shinde
 *
 */
@Provider
@RequiredArgsConstructor(onConstructor = @_(@Inject))
@Singleton
@Slf4j
public class AlchemyExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    /**
     * The response mapper.
     */
    private final ThrowableToResponseMapper responseMapper;

    /*
     * (non-Javadoc)
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
     */
    @Override
    public Response toResponse(final Exception exception) {
        log.error("{}", exception);
        return responseMapper.apply(exception);
    }

}
