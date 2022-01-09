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
import org.apache.commons.csv.parser.CSVParser;
import org.apache.commons.csv.parser.ICSVParser;
import org.apache.commons.csv.printer.CSVPrinter;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JiraCsv249Test {

    @Test
    public void testJiraCsv249() throws IOException {
        final CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setEscape('\\').build();
        final StringWriter stringWriter = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(stringWriter, csvFormat)) {
            printer.printRecord("foo \\", "bar");
        }
        final StringReader stringReader = new StringReader(stringWriter.toString());
        final List<CSVRecord> records;
        try (ICSVParser parser = new CSVParser(stringReader, csvFormat)) {
            records = parser.getRecords();
        }
        records.forEach(record -> {
            assertEquals("foo \\", record.get(0));
            assertEquals("bar", record.get(1));
        });

    }
}
