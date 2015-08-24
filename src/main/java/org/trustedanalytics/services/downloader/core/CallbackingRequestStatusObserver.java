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
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.trustedanalytics.cloud.auth.HeaderAddingHttpInterceptor;

public class CallbackingRequestStatusObserver implements RequestStatusObserver {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CallbackingRequestStatusObserver.class);

    private DownloadRequest request;
    private RestTemplate restTemplate;

    public CallbackingRequestStatusObserver(DownloadRequest request) {
        this.request = request;
        this.restTemplate = oAuthRestTemplate(request.getToken());
    }

    private RestTemplate oAuthRestTemplate(String token) {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(Collections.singletonList(
                new HeaderAddingHttpInterceptor(HttpHeaders.AUTHORIZATION, "bearer " + token)));
        return template;
    }

    private void notifyChange() {
        if (request.getCallback() == null) {
            return;
        }
        try {
            String status =
                    restTemplate.postForObject(request.getCallback().toString(), request, String.class);
            LOGGER.info("Notify on {} returned: {}", request, status);
        } catch (RestClientException e) {
            LOGGER.warn("Notify failed for {} with: {}", request, e.getMessage());
        }
    }

    @Override
    public void notifyFinishedSuccess() {
        notifyChange();
    }

    @Override
    public void notifyFinishedFailed(IOException cause) {
        notifyChange();
    }

    @Override
    public void notifyProgress(long totalBytes) {
        notifyChange();
    }

    @Override
    public void notifyStarted() {
        notifyChange();
    }

}
