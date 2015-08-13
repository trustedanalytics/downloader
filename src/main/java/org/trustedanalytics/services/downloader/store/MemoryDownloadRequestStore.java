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
package org.trustedanalytics.services.downloader.store;

import org.trustedanalytics.services.downloader.core.DownloadRequest;
import org.trustedanalytics.services.downloader.utils.MemoryUniqGeneratedKeysStore;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class MemoryDownloadRequestStore implements DownloadRequestsStore {

    private MemoryUniqGeneratedKeysStore<DownloadRequest> store =
            new MemoryUniqGeneratedKeysStore<>();

    @Override
    public DownloadRequest add(DownloadRequest downloadRequest) {
        String id = store.put(downloadRequest);
        downloadRequest.setId(id);
        return downloadRequest;
    }

    @Override
    public DownloadRequest get(String id) {
        return store.get(id);
    }

    @Override
    public boolean has(String id) {
        return store.has(id);
    }
}
