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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JiraCsv149Test {

    private static final String CR_LF = "\r\n";

    @Test
    public void testJiraCsv149EndWithEOL() throws IOException {
        testJiraCsv149EndWithEolAtEof(true);
    }

    @Test
    private void testJiraCsv149EndWithEolAtEof(final boolean eolAtEof) throws IOException {
        String source = "A,B,C,D" + CR_LF + "a1,b1,c1,d1" + CR_LF + "a2,b2,c2,d2";
        if (eolAtEof) {
            source += CR_LF;
        }
        final StringReader records = new StringReader(source);
        // @formatter:off
        CSVFormat format = CSVFormatPredefinedFormats.RFC4180.getFormat();
        format.setHeader();
        format.setSkipHeaderRecord(true);
        format.setQuoteCharacter('"');

        // @formatter:on
        int lineCounter = 2;
        try (final ICSVParser parser = new CSVParser(records, format)) {
            for (final CSVRecord record : parser) {
                assertEquals(lineCounter++, parser.getCurrentLineNumber());
            }
        }
    }

    @Test
    public void testJiraCsv149EndWithoutEOL() throws IOException {
        testJiraCsv149EndWithEolAtEof(false);
    }
}
