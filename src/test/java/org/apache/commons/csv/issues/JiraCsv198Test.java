/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.csv.issues;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.parser.CSVParser;
import org.apache.commons.csv.parser.ICSVParser;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JiraCsv198Test {

    @Test
    @Disabled
    public void test() throws UnsupportedEncodingException, IOException {
        final InputStream pointsOfReference = getClass()
            .getResourceAsStream("/org/apache/commons/csv/CSV-198/optd_por_public.csv");
        assertNotNull(pointsOfReference);
        try (@SuppressWarnings("resource")
             ICSVParser parser = new CSVParser(new InputStreamReader(pointsOfReference, StandardCharsets.UTF_8), CSVFormatPredefinedFormats.Default.getFormat())) {
            for (final CSVRecord record : parser) {
                final String locationType = record.get("location_type");
                assertNotNull(locationType);
            }
        }
    }

}
