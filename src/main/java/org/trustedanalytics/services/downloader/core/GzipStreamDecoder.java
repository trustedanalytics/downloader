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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.detect.MagicDetector;
import org.apache.tika.mime.MediaType;

public class GzipStreamDecoder implements StreamDecoder {

    private final static byte[] GZIP_MAGIC = {(byte) 0x1f, (byte) 0x08b};

    private Detector gzipDetector = new MagicDetector(
            MediaType.application("gzip"), GZIP_MAGIC);

    StreamDecoder pipelineDecoder = null;

    public GzipStreamDecoder() {

    }

    public GzipStreamDecoder(StreamDecoder decoder) {
        pipelineDecoder = decoder;
    }

    /*
     * 
     * This method detects and decodes gzip content. In case when content is not
     * gzipped, it's passing the stream without changes.
     */
    @Override
    public InputStream tryToDecode(InputStream in) throws IOException {

        BufferedInputStream bin;

        if (pipelineDecoder != null) {
            bin = new BufferedInputStream(pipelineDecoder.tryToDecode(in));
        } else {
            bin = new BufferedInputStream(in);
        }

        MediaType mediaType = gzipDetector.detect(bin, null);

        if (mediaType.toString().contains("gzip")) {

            return new GZIPInputStream(bin);
        }

        return bin;
    }

}
