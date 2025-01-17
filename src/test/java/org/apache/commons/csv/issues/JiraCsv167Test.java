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
import org.apache.commons.csv.format.QuoteMode;
import org.apache.commons.csv.parser.CSVParser;
import org.apache.commons.csv.parser.ICSVParser;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JiraCsv167Test {

    private Reader getTestReader() {
        return new InputStreamReader(
            ClassLoader.getSystemClassLoader().getResourceAsStream("org/apache/commons/csv/csv-167/sample1.csv"));
    }

    @Test
    public void parse() throws IOException {
        int totcomment = 0;
        int totrecs = 0;
        try (final Reader reader = getTestReader(); final BufferedReader br = new BufferedReader(reader)) {
            String s = null;
            boolean lastWasComment = false;
            while ((s = br.readLine()) != null) {
                if (s.startsWith("#")) {
                    if (!lastWasComment) { // comments are merged
                        totcomment++;
                    }
                    lastWasComment = true;
                } else {
                    totrecs++;
                    lastWasComment = false;
                }
            }
        }

        // @formatter:off
        CSVFormat format = new CSVFormat(",", '"', QuoteMode.ALL,
                '#', '\\', true,
                true, "\n", "NULL", new String[] {"headerComment"},
                new String[]{"author", "title", "publishDate"}, false,
                false, true, true,
                true, true, true);

        // @formatter:on
        int comments = 0;
        int records = 0;
        try (final Reader reader = getTestReader(); final ICSVParser parser = new CSVParser(reader, format)) {
            for (final CSVRecord csvRecord : parser) {
                records++;
                if (csvRecord.hasComment()) {
                    comments++;
                }
            }
        }
        // Comment lines are concatenated, in this example 4 lines become 2 comments.
        assertEquals(totcomment, comments);
        assertEquals(totrecs, records); // records includes the header
    }
}
