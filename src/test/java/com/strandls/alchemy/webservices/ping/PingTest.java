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

package com.strandls.alchemy.webservices.ping;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;

import com.strandls.alchemy.rest.client.AlchemyRestClientFactory;
import com.strandls.alchemy.webservices.common.status.Status;
import com.strandls.alchemy.webservices.testbase.WebserviceTestBase;

/**
 * Unit tests for {@link Ping}.
 *
 * @author Ashish Shinde
 */
public class PingTest extends WebserviceTestBase {
    /**
     * Client generation factory.
     */
    @Inject
    AlchemyRestClientFactory clientFactory;

    /**
     * Test method for {@link Ping#ping()}.
     *
     * @throws Exception
     */
    @Test
    public void testPing() throws Exception {
        final Status status = clientFactory.getInstance(Ping.class).ping();
        assertEquals(Status.RUNNING, status);
    }
}
