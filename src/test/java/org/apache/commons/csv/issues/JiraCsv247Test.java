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

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class JiraCsv247Test {

    @Test
    public void testHeadersMissingOneColumnWhenAllowingMissingColumnNames() throws Exception {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setHeader();
        format.setAllowMissingColumnNames(true);

        assertTrue(format.getAllowMissingColumnNames(), "We should allow missing column names");

        final Reader in = new StringReader("a,,c,d,e\n1,2,3,4,5\nv,w,x,y,z");
        try (final ICSVParser parser = new CSVParser(in, format)) {
            assertEquals(Arrays.asList("a", "", "c", "d", "e"), parser.getHeaderNames());
            final Iterator<CSVRecord> iterator = parser.iterator();
            CSVRecord record = iterator.next();
            assertEquals("1", record.get(0));
            assertEquals("2", record.get(1));
            assertEquals("3", record.get(2));
            assertEquals("4", record.get(3));
            assertEquals("5", record.get(4));
            record = iterator.next();
            assertEquals("v", record.get(0));
            assertEquals("w", record.get(1));
            assertEquals("x", record.get(2));
            assertEquals("y", record.get(3));
            assertEquals("z", record.get(4));
            assertFalse(iterator.hasNext());
        }
    }

    @Test
    public void testHeadersMissingThrowsWhenNotAllowingMissingColumnNames() {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setHeader();

        assertFalse(format.getAllowMissingColumnNames(), "By default we should not allow missing column names");

        assertThrows(IllegalArgumentException.class, () -> {
            try (final Reader reader = new StringReader("a,,c,d,e\n1,2,3,4,5\nv,w,x,y,z");
                 ICSVParser parser = new CSVParser(reader, format);) {
                // should fail
            }
        }, "1 missing column header is not allowed");

        assertThrows(IllegalArgumentException.class, () -> {
            try (final Reader reader = new StringReader("a,,c,d,\n1,2,3,4,5\nv,w,x,y,z");
                 ICSVParser parser = new CSVParser(reader, format);) {
                // should fail
            }
        }, "2+ missing column headers is not allowed!");
    }
}
