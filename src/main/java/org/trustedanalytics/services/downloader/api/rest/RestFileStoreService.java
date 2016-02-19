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
package org.trustedanalytics.services.downloader.api.rest;

import org.trustedanalytics.store.ObjectStoreFactory;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class RestFileStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestFileStoreService.class);

    @Autowired
    private ObjectStoreFactory<UUID> objectStoreFactory;

    @ApiOperation("Removes previously downloaded file")
    @RequestMapping(value = "/rest/filestore/orgId/{orgId}/fileId/{id}/", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID orgId, @PathVariable String id)
            throws IOException, LoginException, InterruptedException {
        LOGGER.info("delete(org: {}, id: {})", orgId, id);
        objectStoreFactory.create(orgId).remove(id);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(value = INTERNAL_SERVER_ERROR, reason = "Storage problem")
    public void ioExceptionHandler() {

    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(NOT_FOUND)
    public void noSuchElementExceptionHandler() {

    }
}
