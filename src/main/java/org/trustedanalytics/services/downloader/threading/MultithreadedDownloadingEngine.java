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
package org.trustedanalytics.services.downloader.threading;

import org.trustedanalytics.services.downloader.core.DownloadRequest;
import org.trustedanalytics.services.downloader.core.DownloadingEngine;
import org.trustedanalytics.services.downloader.core.DownloadingStrategy;
import org.trustedanalytics.services.downloader.core.IOStreamsProvider;
import org.trustedanalytics.services.downloader.core.RequestStatusObserver;
import org.trustedanalytics.services.downloader.core.RequestStatusObserverFactory;
import org.trustedanalytics.store.ObjectStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

@Component
public class MultithreadedDownloadingEngine implements DownloadingEngine {

    @Resource(name = "topLevelExecutors")
    private ExecutorService executorService;
    @Autowired
    private DownloadingStrategy downloadingStrategy;
    @Autowired
    private IOStreamsProvider ioStreamsProvider;
    @Autowired
    private RequestStatusObserverFactory requestStatusObserverFactory;
    @Autowired
    private Function<UUID, ObjectStore> objectStoreFactory;

    @Override
    public void download(DownloadRequest downloadRequest) {
        RequestStatusObserver requestStatusObserver =
            requestStatusObserverFactory.getNew(downloadRequest);
        DownloadTask downloadTask = new DownloadTask(
            downloadRequest,
            ioStreamsProvider,
            downloadingStrategy,
            requestStatusObserver,
            objectStoreFactory.apply(downloadRequest.getOrgUUID())
        );
        downloadRequest.setState(DownloadRequest.State.QUEUED);
        // ^^^ maybe it should be after execute but it wont work with eager executor
        executorService.execute(downloadTask);
    }


}
