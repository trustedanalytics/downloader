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

import java.io.IOException;

/**
 * API to inform watcher(s) about progress of job downloading
 */
public interface RequestStatusObserver {

    public static final RequestStatusObserver EMPTY_REQUEST_STATUS_OBSERVER =
            new RequestStatusObserver() {
            };

    default void notifyStarted() {
    }

    default void notifyProgress(long totalBytes) {
    }

    default void notifyFinishedSuccess() {
    }

    default void notifyFinishedFailed(IOException cause) {
    }
}
