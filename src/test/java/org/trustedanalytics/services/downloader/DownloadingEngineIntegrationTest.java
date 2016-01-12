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

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

import org.trustedanalytics.services.downloader.core.DownloadRequest;
import org.trustedanalytics.services.downloader.core.RequestStatusObserver;
import org.trustedanalytics.services.downloader.threading.MultithreadedDownloadingEngine;
import org.trustedanalytics.store.ObjectStore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DownloaderConfigInTests.class})
public class DownloadingEngineIntegrationTest {

    private static final byte[] SIMPLE_DATA = new byte[]{1, 2, 3, 4, 5, 6};

    @Autowired
    RequestStatusObserver mockedRequestStatusObserver;

    @Autowired
    MultithreadedDownloadingEngine downloadingEngine;

    @Autowired
    ObjectStore objectStore;

    @Autowired
    String TOKEN;

    @Test(timeout = 1000 * 1000) // Not needed now
    public void download_notExistingSource_iOExceptionThrown() throws InterruptedException {
        UUID orgUUID = UUID.randomUUID();
        DownloadRequest downloadRequest =
                new DownloadRequest(makeURI("os://somehost.com/notexisting"), orgUUID, TOKEN);
        downloadingEngine.download(downloadRequest);
        Thread.sleep(1000); // TTA: kk: use notifier with barriers?

        InOrder inOrder = inOrder(mockedRequestStatusObserver);
        inOrder.verify(mockedRequestStatusObserver).notifyStarted();
        inOrder.verify(mockedRequestStatusObserver).notifyFinishedFailed(any(IOException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test(timeout = 1000 * 1000) // Not needed now
    public void download_existingFile_dataAreEqual() throws IOException, InterruptedException {
        String id1 = objectStore.save(SIMPLE_DATA);
        UUID orgUUID = UUID.randomUUID();
        DownloadRequest downloadRequest = new DownloadRequest(makeURI("os://somehost.com/" + id1), orgUUID, TOKEN);

        downloadingEngine.download(downloadRequest);
        Thread.sleep(1000); // TTA: kk: use notifier with barriers?

        InOrder inOrder = inOrder(mockedRequestStatusObserver);
        inOrder.verify(mockedRequestStatusObserver).notifyStarted();
//        inOrder.verify(mockedRequestStatusObserver).notifyProgress(eq((long) SIMPLE_DATA.length));
        // ^^^ due to using simpleDownloadingStrategy
        inOrder.verify(mockedRequestStatusObserver).notifyFinishedSuccess();
        inOrder.verifyNoMoreInteractions();

        InputStream savedInputStream = objectStore.getContent(id1);
        byte[] copiedData = ByteStreams.toByteArray(savedInputStream);
        assertTrue(Arrays.equals(SIMPLE_DATA, copiedData));
    }

    private static URI makeURI(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }
}
