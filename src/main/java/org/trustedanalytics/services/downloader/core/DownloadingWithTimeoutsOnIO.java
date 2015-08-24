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

import com.google.common.base.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.trustedanalytics.store.ObjectStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// TTA: make download default method of interface and write/read abstract?


/**
 * Read and write are timeboxed.
 * Requires executor that is different than the one that is calling this code
 * <p>
 * TODO: fix it!
 */
public class DownloadingWithTimeoutsOnIO implements DownloadingStrategy {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CallbackingRequestStatusObserver.class);

    private static final int PACKAGE_SIZE = 1024 * 1024; // 1 MB

    private final ExecutorService operationsExecutorService;

    private int timeoutForOperations;

    public DownloadingWithTimeoutsOnIO(ExecutorService operationsExecutorService) {
        this(operationsExecutorService, 10 * 60); // 10 minutes
    }

    /**
     * @param timeoutForOperations in seconds
     */
    public DownloadingWithTimeoutsOnIO(
            ExecutorService operationsExecutorService,
            int timeoutForOperations) {
        this.operationsExecutorService = operationsExecutorService;
        this.timeoutForOperations = timeoutForOperations;
    }

    /**
     * Retrieve result from future and fail if it takes more than specified amount of time.
     * Propagate any IO related exceptions. Timeout is also turned into one of those
     */
    protected <T> T evaluateFutureWithTimeout(Future<T> future) throws IOException {
        try {
            return future.get(timeoutForOperations, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            throw Throwables.propagate(ex);
        } catch (ExecutionException ex) {
            Throwables.propagateIfInstanceOf(ex.getCause(), IOException.class);
            throw Throwables.propagate(ex);
        } catch (TimeoutException e) {
            LOGGER.warn("Exception message: {}", e.getMessage());
            throw new InterruptedByTimeoutException();
        }
    }

    protected int read(InputStream in, byte[] data) throws IOException {
        Callable<Integer> reading = () -> in.read(data, 0, PACKAGE_SIZE);

        Future<Integer> future = operationsExecutorService.submit(reading);
        return evaluateFutureWithTimeout(future);
    }

    protected void write(OutputStream out, byte[] data, int count) throws IOException {
        Callable writing = () -> {
            out.write(data, 0, count);
            return null;
        };

        Future future = operationsExecutorService.submit(writing);
        evaluateFutureWithTimeout(future);
    }

    @Override
    public String download(InputStream in, ObjectStore store,
                           RequestStatusObserver requestStatusObserver) throws IOException {
        int count;
        byte[] data = new byte[PACKAGE_SIZE];
        while ((count = read(in, data)) != -1) {
//            write(out, data, count); TODO: it is broken now
            requestStatusObserver.notifyProgress(count);
        }
        return "";
    }
}
