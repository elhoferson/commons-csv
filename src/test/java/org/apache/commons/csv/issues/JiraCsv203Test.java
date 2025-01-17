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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.printer.CSVPrinter;
import org.apache.commons.csv.format.QuoteMode;
import org.junit.jupiter.api.Test;

/**
 * JIRA: <a href="https://issues.apache.org/jira/browse/CSV-203">withNullString value is printed without quotes when
 * QuoteMode.ALL is specified</a>
 */
public class JiraCsv203Test {

    @Test
    public void testQuoteModeAll() throws Exception {
        // @formatter:off

        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setNullString("N/A");
        format.setIgnoreSurroundingSpaces(true);
        format.setQuoteMode(QuoteMode.ALL);

        // @formatter:on
        final StringBuilder buffer = new StringBuilder();
        try (final CSVPrinter printer = new CSVPrinter(buffer, format)) {
            printer.printRecord(null, "Hello", null, "World");
        }
        assertEquals("\"N/A\",\"Hello\",\"N/A\",\"World\"\r\n", buffer.toString());
    }

    @Test
    public void testQuoteModeAllNonNull() throws Exception {
        // @formatter:off

        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setNullString("N/A");
        format.setIgnoreSurroundingSpaces(true);
        format.setQuoteMode(QuoteMode.ALL_NON_NULL);

        // @formatter:on
        final StringBuilder buffer = new StringBuilder();
        try (final CSVPrinter printer = new CSVPrinter(buffer, format)) {
            printer.printRecord(null, "Hello", null, "World");
        }
        assertEquals("N/A,\"Hello\",N/A,\"World\"\r\n", buffer.toString());
    }

    @Test
    public void testQuoteModeMinimal() throws Exception {
        // @formatter:off

        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setNullString("N/A");
        format.setIgnoreSurroundingSpaces(true);
        format.setQuoteMode(QuoteMode.MINIMAL);

        // @formatter:on
        final StringBuilder buffer = new StringBuilder();
        try (final CSVPrinter printer = new CSVPrinter(buffer, format)) {
            printer.printRecord(null, "Hello", null, "World");
        }
        assertEquals("N/A,Hello,N/A,World\r\n", buffer.toString());
    }

    @Test
    public void testQuoteModeNonNumeric() throws Exception {
        // @formatter:off

        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setNullString("N/A");
        format.setIgnoreSurroundingSpaces(true);
        format.setQuoteMode(QuoteMode.NON_NUMERIC);

        // @formatter:on
        final StringBuilder buffer = new StringBuilder();
        try (final CSVPrinter printer = new CSVPrinter(buffer, format)) {
            printer.printRecord(null, "Hello", null, "World");
        }
        assertEquals("N/A,\"Hello\",N/A,\"World\"\r\n", buffer.toString());
    }

    @Test
    public void testWithEmptyValues() throws Exception {
        // @formatter:off

        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setNullString("N/A");
        format.setIgnoreSurroundingSpaces(true);
        format.setQuoteMode(QuoteMode.ALL);

        // @formatter:on
        final StringBuilder buffer = new StringBuilder();
        try (final CSVPrinter printer = new CSVPrinter(buffer, format)) {
            printer.printRecord("", "Hello", "", "World");
            // printer.printRecord(new Object[] { null, "Hello", null, "World" });
        }
        assertEquals("\"\",\"Hello\",\"\",\"World\"\r\n", buffer.toString());
    }

    @Test
    public void testWithoutNullString() throws Exception {
        // @formatter:off

        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setIgnoreSurroundingSpaces(true);
        format.setQuoteMode(QuoteMode.ALL);

        // @formatter:on
        final StringBuilder buffer = new StringBuilder();
        try (final CSVPrinter printer = new CSVPrinter(buffer, format)) {
            printer.printRecord(null, "Hello", null, "World");
        }
        assertEquals(",\"Hello\",,\"World\"\r\n", buffer.toString());
    }

    @Test
    public void testWithoutQuoteMode() throws Exception {
        // @formatter:off

        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setNullString("N/A");
        format.setIgnoreSurroundingSpaces(true);

        // @formatter:on
        final StringBuilder buffer = new StringBuilder();
        try (final CSVPrinter printer = new CSVPrinter(buffer, format)) {
            printer.printRecord(null, "Hello", null, "World");
        }
        assertEquals("N/A,Hello,N/A,World\r\n", buffer.toString());
    }
}
