/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.api.common;

import org.openhubframework.openhub.api.asynch.model.TraceHeader;

import org.apache.camel.Exchange;

/**
 * Constants regarding to holding information in {@link Exchange}
 *
 * @author Tomas Hanus
 * @since 0.4
 */
public final class ExchangeConstants {

    /**
     * Header value that holds {@link TraceHeader}.
     */
    public static final String TRACE_HEADER = "ASYNCH_TRACE_HEADER";

    private ExchangeConstants() {
    }
}
