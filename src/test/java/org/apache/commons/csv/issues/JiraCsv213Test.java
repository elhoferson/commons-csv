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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Tests https://issues.apache.org/jira/browse/CSV-213
 *
 * This is normal behavior with the current architecture: The iterator() API presents an object that is backed by data
 * in the CSVParser as the parser is streaming over the file. The CSVParser is like a forward-only stream. When you
 * create a new Iterator you are only created a new view on the same position in the parser's stream. For the behavior
 * you want, you need to open a new CSVParser.
 */
@Disabled
public class JiraCsv213Test {

    private void createEndChannel(final File csvFile) {
        // @formatter:off
        CSVFormat csvFormat = CSVFormatPredefinedFormats.Default.getFormat();
        csvFormat.setDelimiter(";");
        csvFormat.setHeader();
        csvFormat.setSkipHeaderRecord(true);
        csvFormat.setRecordSeparator("\n");
        csvFormat.setQuoteMode(QuoteMode.ALL);

        // @formatter:on
        try (Reader reader = Files.newBufferedReader(csvFile.toPath(), StandardCharsets.UTF_8);
            ICSVParser parser = new CSVParser(reader, csvFormat)) {
            if (parser.iterator().hasNext()) {
                // System.out.println(parser.getCurrentLineNumber());
                // System.out.println(parser.getRecordNumber());
                // get only first record we don't need other's
                parser.iterator().next(); // this fails
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Error while adding end channel to CSV", e);
        }
    }

    @Test
    public void test() {
        createEndChannel(new File("src/test/resources/CSV-213/999751170.patch.csv"));
    }
}
