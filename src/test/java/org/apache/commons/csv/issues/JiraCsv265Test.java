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
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests [CSV-265] {@link CSVRecord#getCharacterPosition()} returns the correct position after encountering a comment.
 */
public class JiraCsv265Test {

    @Test
    public void testCharacterPositionWithComments() throws IOException {
        // @formatter:off
        final String csv = "# Comment1\n"
                         + "Header1,Header2\n"
                         + "# Comment2\n"
                         + "Value1,Value2\n"
                         + "# Comment3\n"
                         + "Value3,Value4\n"
                         + "# Comment4\n";
        final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setCommentMarker('#')
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();
        // @formatter:on
        try (final ICSVParser parser = new CSVParser(new StringReader(csv), csvFormat)) {
            final Iterator<CSVRecord> itr = parser.iterator();
            final CSVRecord record1 = itr.next();
            assertEquals(csv.indexOf("# Comment2"), record1.getCharacterPosition());
            final CSVRecord record2 = itr.next();
            assertEquals(csv.indexOf("# Comment3"), record2.getCharacterPosition());
        }
    }

    @Test
    public void testCharacterPositionWithCommentsSpanningMultipleLines() throws IOException {
        // @formatter:off
        final String csv = "# Comment1\n"
                         + "# Comment2\n"
                         + "Header1,Header2\n"
                         + "# Comment3\n"
                         + "# Comment4\n"
                         + "Value1,Value2\n"
                         + "# Comment5\n"
                         + "# Comment6\n"
                         + "Value3,Value4";
        final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setCommentMarker('#')
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();
        // @formatter:on
        try (final ICSVParser parser = new CSVParser(new StringReader(csv), csvFormat)) {
            final Iterator<CSVRecord> itr = parser.iterator();
            final CSVRecord record1 = itr.next();
            assertEquals(csv.indexOf("# Comment3"), record1.getCharacterPosition());
            final CSVRecord record2 = itr.next();
            assertEquals(csv.indexOf("# Comment5"), record2.getCharacterPosition());
        }
    }

}
