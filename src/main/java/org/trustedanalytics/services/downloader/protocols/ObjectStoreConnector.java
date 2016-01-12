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

import org.trustedanalytics.services.downloader.core.Connector;
import org.trustedanalytics.store.ObjectStore;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
public class ObjectStoreConnector implements Connector {

    private static final ImmutableList<String> SUPPORTED_SCHEMES = ImmutableList.of("os");

    private BiFunction<UUID, String, ObjectStore> objectStoreFactory;

    @Autowired
    public ObjectStoreConnector(BiFunction<UUID, String, ObjectStore> objectStoreFactory) {
        this.objectStoreFactory = objectStoreFactory;
    }

    @Override
    public InputStream getInputStream(URI source, Properties properties) throws IOException {
        UUID orgUUID = UUID.fromString(properties.getProperty("orgUUID"));
        String token = properties.getProperty("token");
        return objectStoreFactory.apply(orgUUID, token)
            .getContent(source.getPath().substring(1)); // we strip first /
    }

    @Override
    public OutputStream getOutputStream(URI target, Properties properties)
        throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableList<String> getSupportedSchemes() {
        return SUPPORTED_SCHEMES;
    }
}
