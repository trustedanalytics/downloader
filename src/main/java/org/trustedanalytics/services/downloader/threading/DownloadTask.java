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
import org.trustedanalytics.services.downloader.core.DownloadingStrategy;
import org.trustedanalytics.services.downloader.core.IOStreamsProvider;
import org.trustedanalytics.services.downloader.core.RequestStatusObserver;
import org.trustedanalytics.store.ObjectStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Single tasks is responsible for one download.
 * It is runnable and all dependencies are passed in constructor
 */
public class DownloadTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadTask.class);

    private final DownloadRequest downloadRequest;
    private final IOStreamsProvider ioStreamsProvider;
    private final DownloadingStrategy downloadingStrategy;
    private final RequestStatusObserver requestStatusObserver;
    private final ObjectStore objectStore;

    public DownloadTask(
            DownloadRequest downloadRequest,
            IOStreamsProvider ioStreamsProvider,
            DownloadingStrategy downloadingStrategy,
            RequestStatusObserver requestStatusObserver,
            ObjectStore objectStore) {
        this.downloadRequest = downloadRequest;
        this.ioStreamsProvider = ioStreamsProvider;
        this.downloadingStrategy = downloadingStrategy;
        this.requestStatusObserver = requestStatusObserver;
        this.objectStore = objectStore;
    }

    @Override
    public void run() {
        downloadRequest.setState(DownloadRequest.State.IN_PROGRESS);
        LOGGER.info("Starting task: {}", downloadRequest);
        requestStatusObserver.notifyStarted();
        try (InputStream in = ioStreamsProvider
                .getInputStream(downloadRequest.getSource(), getInputStreamProperties())) {
            String objectId = downloadingStrategy.download(in, objectStore, requestStatusObserver);
            LOGGER.info("Finished task: {}", downloadRequest);
            downloadRequest.setSavedObjectId(objectId);
            downloadRequest.setObjectStoreId(objectStore.getId());
            downloadRequest.setState(DownloadRequest.State.DONE);
            requestStatusObserver.notifyFinishedSuccess();
        } catch (IOException | LoginException | InterruptedException e) {
            // NOTE: there might be RuntimeException here or total death of this service
            // - we can miss notify..Failed
            LOGGER.error("Failed task: {}", downloadRequest, e);
            downloadRequest.setState(DownloadRequest.State.FAILED);
            requestStatusObserver.notifyFinishedFailed(e);
        }
    }

    private Properties getInputStreamProperties() {
        Properties properties = new Properties();
        properties.setProperty("token", downloadRequest.getToken());
        properties.setProperty("orgUUID", downloadRequest.getOrgUUID().toString());
        return properties;
    }
}
