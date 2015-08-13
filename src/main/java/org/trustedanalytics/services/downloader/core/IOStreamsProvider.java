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
package org.trustedanalytics.services.downloader.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.trustedanalytics.services.downloader.protocols.FileConnector;
import org.trustedanalytics.services.downloader.protocols.HttpConnector;
import org.trustedanalytics.services.downloader.protocols.ObjectStoreConnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Provides (Input|Output)Streams for given URI and Properties
 */
@Component
public class IOStreamsProvider {

    // TODO: inject list of connectors
    private Map<String, Connector> supportedSchemes;

    @Autowired
    public IOStreamsProvider(FileConnector fileConnector, HttpConnector httpConnector,
                             ObjectStoreConnector objectStoreConnector) {
        this(Lists.newArrayList(fileConnector, httpConnector, objectStoreConnector));
    }

    public IOStreamsProvider(List<Connector> connectors) {
        supportedSchemes = new HashMap<>(connectors.size());
        for (Connector connector : connectors) {
            for (String scheme : connector.getSupportedSchemes()) {
                String normalizedScheme = normalizeScheme(scheme);
                supportedSchemes.put(normalizedScheme, connector);
            }
        }
    }

    private String normalizeScheme(String scheme) {
        return scheme.toLowerCase(Locale.getDefault());
    }

    private Connector getConnectorForScheme(String scheme) {
        String normalizedScheme = normalizeScheme(scheme);
        Preconditions.checkArgument(
                supportedSchemes.containsKey(normalizedScheme),
                "Unsupported scheme: " + scheme);
        return supportedSchemes.get(scheme);
    }

    public InputStream getInputStream(URI in, Properties properties) throws IOException {
        Connector connector = getConnectorForScheme(in.getScheme());
        return connector.getInputStream(in, properties);
    }

    public OutputStream getOutputStream(URI out, Properties properties) throws IOException {
        Connector connector = getConnectorForScheme(out.getScheme());
        return connector.getOutputStream(out, properties);
    }
}
