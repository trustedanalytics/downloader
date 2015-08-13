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


import org.trustedanalytics.services.downloader.DownloaderConfigInTests;
import org.trustedanalytics.store.ObjectStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {DownloaderConfigInTests.class})
public class TestRestFileStoreService {

    @Autowired
    ObjectStore objectStore;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @Before
    public void setupContext() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    public void delete_nonExistingFile_return404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/filestore/1/"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void delete_existingFile_removedAndReturn200_removingSecondTime_return404() throws Exception {
        addDataToStore();

        mockMvc.perform(MockMvcRequestBuilders.delete("/filestore/1/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.delete("/filestore/1/"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    private String addDataToStore() throws IOException {
        byte[] bytes1 = new byte[]{1, 2, 3, 4};

        return objectStore.save(bytes1);
    }


}
