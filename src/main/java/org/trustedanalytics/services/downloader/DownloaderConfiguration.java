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
package org.trustedanalytics.services.downloader;

import org.trustedanalytics.cloud.auth.AuthTokenRetriever;
import org.trustedanalytics.cloud.auth.OAuth2TokenRetriever;
import org.trustedanalytics.services.downloader.core.CallbackingRequestStatusObserver;
import org.trustedanalytics.services.downloader.core.DownloadingStrategy;
import org.trustedanalytics.services.downloader.core.GzipStreamDecoder;
import org.trustedanalytics.services.downloader.core.RequestStatusObserverFactory;
import org.trustedanalytics.services.downloader.core.SimpleDownloadingStrategy;
import org.trustedanalytics.services.downloader.core.StreamDecoder;
import org.trustedanalytics.services.downloader.core.ZipStreamDecoder;
import org.trustedanalytics.services.downloader.store.DownloadRequestsStore;
import org.trustedanalytics.services.downloader.store.MemoryDownloadRequestStore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;

@Configuration
@ComponentScan(basePackages = {"org.trustedanalytics.clients"})
public class DownloaderConfiguration {

    private static final int BASE_DOWNLOADING_THREADS_COUNT = 25;

    @Resource(name = "executorForIO")
    ExecutorService iOExecutor;

    @Bean(name = {"topLevelExecutors"}, destroyMethod = "shutdown")
    public ExecutorService topLevelExecutorService() {
        return Executors.newFixedThreadPool(BASE_DOWNLOADING_THREADS_COUNT);
    }

    @Bean(name = {"executorForIO"}, destroyMethod = "shutdown")
    public ExecutorService iOExecutor() {
        return Executors.newFixedThreadPool(BASE_DOWNLOADING_THREADS_COUNT);
    }

    @Bean
    public DownloadingStrategy downloadStrategy() {
        return new SimpleDownloadingStrategy(streamDecoder());
    }

    @Bean
    public StreamDecoder streamDecoder() {
        // decoders can be connected into the pipeline, this is similar to Decorator pattern
        return new ZipStreamDecoder(gzipStreamDecoder());
    }

    @Bean
    public StreamDecoder gzipStreamDecoder() {
        return new GzipStreamDecoder();
    }

    @Bean
    public RequestStatusObserverFactory requestStatusObserverFactory() {
        return CallbackingRequestStatusObserver::new;
    }

    @Bean
    public AuthTokenRetriever authTokenRetriever() {
        return new OAuth2TokenRetriever();
    }

    @Bean
    public DownloadRequestsStore downloadRequestsStore() {
        return new MemoryDownloadRequestStore();
    }
}
