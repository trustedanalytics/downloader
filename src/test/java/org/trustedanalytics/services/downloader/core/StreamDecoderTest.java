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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@RunWith(value = Parameterized.class)
public class StreamDecoderTest {

    StreamDecoder decoder;

    @Before
    public void initialize() {
        decoder = new ZipStreamDecoder(new GzipStreamDecoder());
    }

    @Parameter
    public String fileName;


    @Test
    public void testStream() throws IOException {
        InputStream in;

        in = getClass().getResourceAsStream("/" + fileName);

        String theString = IOUtils.toString(decoder.tryToDecode(in), "UTF-8");
        assertTrue(theString.contains("OK"));

    }


    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{{"stream.zip"}, {"stream.txt"}, {"stream.tar.gz"}});
    }


}
