/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.services.downloader.protocols;

import com.google.common.collect.ImmutableList;

import org.trustedanalytics.services.downloader.core.Connector;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

/**
 * Provides download from http(s) protocols. Upload is not possible.
 * <p>
 * TTA: can we make form request and that way try to provide getOutputStream?
 */
@Component
public class HttpConnector implements Connector {

    public static final ImmutableList<String> SUPPORTED_SCHEMES =
            ImmutableList.of("http", "https");

    @Override
    public InputStream getInputStream(URI source, Properties properties) throws IOException {
        URL url = source.toURL();
        return url.openStream();
    }

    @Override
    public OutputStream getOutputStream(URI target, Properties properties) {
        throw new UnsupportedOperationException("Can't upload file to http(s)");
    }

    @Override
    public ImmutableList<String> getSupportedSchemes() {
        return SUPPORTED_SCHEMES;
    }
}
