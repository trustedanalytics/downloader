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
import org.trustedanalytics.services.downloader.core.RequestStatusObserver;
import org.trustedanalytics.services.downloader.core.RequestStatusObserverFactory;
import org.trustedanalytics.services.downloader.core.StreamDecoder;
import org.trustedanalytics.services.downloader.core.ZipStreamDecoder;
import org.trustedanalytics.store.MemoryObjectStore;
import org.trustedanalytics.store.ObjectStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(basePackages = {"org.trustedanalytics.services.downloader"})
public class DownloaderConfigInTests {

    @Resource(name = "executorForIO")
    ExecutorService iOExecutor;

    @Autowired
    public RequestStatusObserver MOCKED_REQUEST_STATUS_OBSERVER;

    @Bean(name = {"topLevelExecutors"}, destroyMethod = "shutdown")
    public ExecutorService topLevelExecutorService() {
        return Executors.newFixedThreadPool(2);
    }

    @Bean(name = {"executorForIO"}, destroyMethod = "shutdown")
    public ExecutorService ioExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    @Bean
    public ObjectStore objectStore() {
        return new MemoryObjectStore();
    }

    @Bean
    public Function<UUID, ObjectStore> objectStoreSupplier(ObjectStore objectStore) {
        return (x) -> objectStore;
    }

    @Bean
    public BiFunction<UUID, String, ObjectStore> objectStoreFactory(ObjectStore objectStore) {
        return (x, y) -> objectStore;
    }

    @Bean
    public RequestStatusObserverFactory requestStatusObserverFactory() {
        return downloadRequest -> MOCKED_REQUEST_STATUS_OBSERVER;
    }

    @Bean
    public AuthTokenRetriever authTokenRetriever() {
        return mock(AuthTokenRetriever.class);
    }

    @Bean
    public String TOKEN() {
        return "jhksdf8723kjhdfsh4i187y91hkajl";
    }

    @Bean
    public RequestStatusObserver mockedRequestStatusObserver() {
        return mock(RequestStatusObserver.class);
    }

    @Bean
    public StreamDecoder streamDecoder() {
        return new ZipStreamDecoder();
    }

}
