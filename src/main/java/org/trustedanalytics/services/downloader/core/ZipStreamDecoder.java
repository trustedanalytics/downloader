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
import java.util.zip.ZipInputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.pkg.ZipContainerDetector;

public class ZipStreamDecoder implements StreamDecoder {

    private Detector zipDetector = new ZipContainerDetector();
    private StreamDecoder pipelineDecoder = null;

    public ZipStreamDecoder() {

    }

    public ZipStreamDecoder(StreamDecoder decoder) {
        pipelineDecoder = decoder;
    }

    /*
     * 
     * This method detects and decodes zip content. In case when content is not zipped,
     * it's passing the stream without changes. 
     * 
     * Limitation: if zip stream contains more than one file, only the first file content
     * is returned.
     * 
     */
    @Override
    public InputStream tryToDecode(InputStream in) throws IOException {

        BufferedInputStream bin;

        if (pipelineDecoder != null) {
            bin = new BufferedInputStream(pipelineDecoder.tryToDecode(in));
        } else {
            bin = new BufferedInputStream(in);
        }

        MediaType mediaType = zipDetector.detect(bin, null);
        if (mediaType.equals(MediaType.APPLICATION_ZIP)) {

            ZipInputStream zin = new ZipInputStream(bin);
            if (zin.getNextEntry() != null) {
                return zin;
            } else {
                throw new IOException("Zip file not valid");
            }
        }

        return bin;
    }
}
