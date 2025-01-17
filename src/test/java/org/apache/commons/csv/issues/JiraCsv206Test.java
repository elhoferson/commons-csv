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
import org.apache.commons.csv.printer.CSVPrinter;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JiraCsv206Test {

    @Test
    public void testJiraCsv206MultipleCharacterDelimiter() throws IOException {
        // Read with multiple character delimiter
        final String source = "FirstName[|]LastName[|]Address\r\nJohn[|]Smith[|]123 Main St.";
        final StringReader reader = new StringReader(source);
        CSVFormat csvFormat = CSVFormatPredefinedFormats.Default.getFormat();
        csvFormat.setDelimiter("[|]");
        CSVRecord record = null;
        try (final ICSVParser ICSVParser = new CSVParser(reader, csvFormat)) {
            final Iterator<CSVRecord> iterator = ICSVParser.iterator();
            record = iterator.next();
            assertEquals("FirstName", record.get(0));
            assertEquals("LastName", record.get(1));
            assertEquals("Address", record.get(2));
            record = iterator.next();
            assertEquals("John", record.get(0));
            assertEquals("Smith", record.get(1));
            assertEquals("123 Main St.", record.get(2));
        }
        // Write with multiple character delimiter
        final String outString = "# Change delimiter to [I]\r\n" + "first name[I]last name[I]address\r\n"
            + "John[I]Smith[I]123 Main St.";
        final String comment = "Change delimiter to [I]";
        // @formatter:off
        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setDelimiter("[I]");
        format.setHeader("first name", "last name", "address");
        format.setCommentMarker('#');
        format.setHeaderComments(comment);

        // @formatter:on
        final StringBuilder out = new StringBuilder();
        try (final CSVPrinter printer = new CSVPrinter(out, format)) {
            printer.print(record.get(0));
            printer.print(record.get(1));
            printer.print(record.get(2));
        }
        final String s = out.toString();
        assertEquals(outString, s);
    }
}
