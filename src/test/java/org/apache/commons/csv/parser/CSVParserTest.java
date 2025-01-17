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

package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatBuilder;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.printer.CSVPrinter;
import org.apache.commons.csv.record.CSVRecord;
import org.apache.commons.csv.util.Utils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.Csv;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.apache.commons.csv.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CSVParserTest
 *
 * The test are organized in three different sections: The 'setter/getter' section, the lexer section and finally the
 * parser section. In case a test fails, you should follow a top-down approach for fixing a potential bug (its likely
 * that the parser itself fails if the lexer has problems...).
 */
public class CSVParserTest {


    @Test
    public void testClose() throws Exception {
        final Reader in = new StringReader("# comment\na,b,c\n1,2,3\nx,y,z");
        final Iterator<CSVRecord> records;
        try (final ICSVParser parser = new CSVParser(in, new CSVFormatBuilder().setCommentMarker('#').setHeaders().build())) {
            records = parser.iterator();
            assertTrue(records.hasNext());
        }
        assertFalse(records.hasNext());
        assertThrows(NoSuchElementException.class, records::next);
    }

    @Test
    public void testDefaultFormat() throws IOException {
        final String code = "" + "a,b#\n" // 1)
            + "\"\n\",\" \",#\n" // 2)
            + "#,\"\"\n" // 3)
            + "# Final comment\n"// 4)
        ;
        final String[][] res = {{"a", "b#"}, {"\n", " ", "#"}, {"#", ""}, {"# Final comment"}};

        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        assertFalse(format.isCommentMarkerSet());
        final String[][] res_comments = {{"a", "b#"}, {"\n", " ", "#"},};

        try (final ICSVParser parser = CSVParser.parse(code, format)) {
            final List<CSVRecord> records = parser.getRecords();
            assertFalse(records.isEmpty());

            Utils.compare("Failed to parse without comments", res, records);

            format = new CSVFormatBuilder().setCommentMarker('#').build();
        }
        try (final ICSVParser parser = CSVParser.parse(code, format)) {
            final List<CSVRecord> records = parser.getRecords();

            Utils.compare("Failed to parse with comments", res_comments, records);
        }
    }


    @Test
    public void testEndOfFileBehaviorCSV() throws Exception {
        final String[] codes = {"hello,\r\n\r\nworld,\r\n", "hello,\r\n\r\nworld,", "hello,\r\n\r\nworld,\"\"\r\n",
            "hello,\r\n\r\nworld,\"\"", "hello,\r\n\r\nworld,\n", "hello,\r\n\r\nworld,", "hello,\r\n\r\nworld,\"\"\n",
            "hello,\r\n\r\nworld,\"\""};
        final String[][] res = {{"hello", ""}, // CSV format ignores empty lines
            {"world", ""}};
        for (final String code : codes) {
            try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Default.getFormat())) {
                final List<CSVRecord> records = parser.getRecords();
                assertEquals(res.length, records.size());
                assertFalse(records.isEmpty());
                for (int i = 0; i < res.length; i++) {
                    assertArrayEquals(res[i], records.get(i).values());
                }
            }
        }
    }

    @Test
    public void testEndOfFileBehaviorExcel() throws Exception {
        final String[] codes = {"hello,\r\n\r\nworld,\r\n", "hello,\r\n\r\nworld,", "hello,\r\n\r\nworld,\"\"\r\n",
            "hello,\r\n\r\nworld,\"\"", "hello,\r\n\r\nworld,\n", "hello,\r\n\r\nworld,", "hello,\r\n\r\nworld,\"\"\n",
            "hello,\r\n\r\nworld,\"\""};
        final String[][] res = {{"hello", ""}, {""}, // Excel format does not ignore empty lines
            {"world", ""}};

        for (final String code : codes) {
            try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Excel.getFormat())) {
                final List<CSVRecord> records = parser.getRecords();
                assertEquals(res.length, records.size());
                assertFalse(records.isEmpty());
                for (int i = 0; i < res.length; i++) {
                    assertArrayEquals(res[i], records.get(i).values());
                }
            }
        }
    }

    @Test
    public void testExcelFormat1() throws IOException {
        final String code = "value1,value2,value3,value4\r\na,b,c,d\r\n  x,,,"
            + "\r\n\r\n\"\"\"hello\"\"\",\"  \"\"world\"\"\",\"abc\ndef\",\r\n";
        final String[][] res = {{"value1", "value2", "value3", "value4"}, {"a", "b", "c", "d"}, {"  x", "", "", ""},
            {""}, {"\"hello\"", "  \"world\"", "abc\ndef", ""}};
        try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Excel.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(res.length, records.size());
            assertFalse(records.isEmpty());
            for (int i = 0; i < res.length; i++) {
                assertArrayEquals(res[i], records.get(i).values());
            }
        }
    }

    @Test
    public void testExcelFormat2() throws Exception {
        final String code = "foo,baar\r\n\r\nhello,\r\n\r\nworld,\r\n";
        final String[][] res = {{"foo", "baar"}, {""}, {"hello", ""}, {""}, {"world", ""}};
        try (final ICSVParser parser = CSVParser.parse(code, CSVFormatPredefinedFormats.Excel.getFormat())) {
            final List<CSVRecord> records = parser.getRecords();
            assertEquals(res.length, records.size());
            assertFalse(records.isEmpty());
            for (int i = 0; i < res.length; i++) {
                assertArrayEquals(res[i], records.get(i).values());
            }
        }
    }

    /**
     * Tests an exported Excel worksheet with a header row and rows that have more columns than the headers
     *
     * @throws Exception
     */
    @Test
    public void testExcelHeaderCountLessThanData() throws Exception {
        final String code = "A,B,C,,\r\na,b,c,d,e\r\n";
        CSVFormat format = CSVFormatPredefinedFormats.Excel.getFormat();
        format.setHeader();
        try (final ICSVParser parser = CSVParser.parse(code, format)) {
            for (final CSVRecord record : parser.getRecords()) {
                assertEquals("a", record.get("A"));
                assertEquals("b", record.get("B"));
                assertEquals("c", record.get("C"));
            }
        }
    }

    @Test
    public void testForEach() throws Exception {
        final List<CSVRecord> records = new ArrayList<>();
        try (final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z")) {
            for (final CSVRecord record : new CSVParser(in, CSVFormatPredefinedFormats.Default.getFormat())) {
                records.add(record);
            }
            assertEquals(3, records.size());
            assertArrayEquals(new String[] {"a", "b", "c"}, records.get(0).values());
            assertArrayEquals(new String[] {"1", "2", "3"}, records.get(1).values());
            assertArrayEquals(new String[] {"x", "y", "z"}, records.get(2).values());
        }
    }

    @Test
    public void testInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setDelimiter(CR).build());
    }

    @Test
    public void testIterator() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z");

        final ICSVParser ICSVParser = new CSVParser(in, CSVFormatPredefinedFormats.Default.getFormat());
        final Iterator<CSVRecord> iterator = ICSVParser.iterator();

        assertTrue(iterator.hasNext());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertArrayEquals(new String[] {"a", "b", "c"}, iterator.next().values());
        assertArrayEquals(new String[] {"1", "2", "3"}, iterator.next().values());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertArrayEquals(new String[] {"x", "y", "z"}, iterator.next().values());
        assertFalse(iterator.hasNext());

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testIteratorSequenceBreaking() throws IOException {
        final String fiveRows = "1\n2\n3\n4\n5\n";

        // Iterator hasNext() shouldn't break sequence
        try (ICSVParser parser = new CSVParser(new StringReader(fiveRows), CSVFormatPredefinedFormats.Default.getFormat())) {

            final Iterator<CSVRecord> iter = parser.iterator();
            int recordNumber = 0;
            while (iter.hasNext()) {
                final CSVRecord record = iter.next();
                recordNumber++;
                assertEquals(String.valueOf(recordNumber), record.get(0));
                if (recordNumber >= 2) {
                    break;
                }
            }
            iter.hasNext();
            while (iter.hasNext()) {
                final CSVRecord record = iter.next();
                recordNumber++;
                assertEquals(String.valueOf(recordNumber), record.get(0));
            }
        }

        // Consecutive enhanced for loops shouldn't break sequence
        try (ICSVParser parser =  new CSVParser(new StringReader(fiveRows), CSVFormatPredefinedFormats.Default.getFormat())) {
            int recordNumber = 0;
            for (final CSVRecord record : parser) {
                recordNumber++;
                assertEquals(String.valueOf(recordNumber), record.get(0));
                if (recordNumber >= 2) {
                    break;
                }
            }
            for (final CSVRecord record : parser) {
                recordNumber++;
                assertEquals(String.valueOf(recordNumber), record.get(0));
            }
        }

        // Consecutive enhanced for loops with hasNext() peeking shouldn't break sequence
        try (ICSVParser parser = new CSVParser(new StringReader(fiveRows), CSVFormatPredefinedFormats.Default.getFormat())) {
            int recordNumber = 0;
            for (final CSVRecord record : parser) {
                recordNumber++;
                assertEquals(String.valueOf(recordNumber), record.get(0));
                if (recordNumber >= 2) {
                    break;
                }
            }
            parser.iterator().hasNext();
            for (final CSVRecord record : parser) {
                recordNumber++;
                assertEquals(String.valueOf(recordNumber), record.get(0));
            }
        }
    }

    @Test
    public void testMappedButNotSetAsOutlook2007ContactExport() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2\nx,y,z");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders("A", "B", "C").setSkipHeaderRecord(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        CSVRecord record;

        // 1st record
        record = records.next();
        assertTrue(record.isMapped("A"));
        assertTrue(record.isMapped("B"));
        assertTrue(record.isMapped("C"));
        assertTrue(record.isSet("A"));
        assertTrue(record.isSet("B"));
        assertFalse(record.isSet("C"));
        assertEquals("1", record.get("A"));
        assertEquals("2", record.get("B"));
        assertFalse(record.isConsistent());

        // 2nd record
        record = records.next();
        assertTrue(record.isMapped("A"));
        assertTrue(record.isMapped("B"));
        assertTrue(record.isMapped("C"));
        assertTrue(record.isSet("A"));
        assertTrue(record.isSet("B"));
        assertTrue(record.isSet("C"));
        assertEquals("x", record.get("A"));
        assertEquals("y", record.get("B"));
        assertEquals("z", record.get("C"));
        assertTrue(record.isConsistent());

        assertFalse(records.hasNext());
    }

    @Test
    @Disabled
    public void testMongoDbCsv() throws Exception {
        try (final ICSVParser parser = CSVParser.parse("\"a a\",b,c" + LF + "d,e,f", CSVFormatPredefinedFormats.MongoDBCsv.getFormat())) {
            final Iterator<CSVRecord> itr1 = parser.iterator();
            final Iterator<CSVRecord> itr2 = parser.iterator();

            final CSVRecord first = itr1.next();
            assertEquals("a a", first.get(0));
            assertEquals("b", first.get(1));
            assertEquals("c", first.get(2));

            final CSVRecord second = itr2.next();
            assertEquals("d", second.get(0));
            assertEquals("e", second.get(1));
            assertEquals("f", second.get(2));
        }
    }

    @Test
    // TODO this may lead to strange behavior, throw an exception if iterator() has already been called?
    public void testMultipleIterators() throws Exception {
        try (final ICSVParser parser = CSVParser.parse("a,b,c" + CRLF + "d,e,f", CSVFormatPredefinedFormats.Default.getFormat())) {
            final Iterator<CSVRecord> itr1 = parser.iterator();

            final CSVRecord first = itr1.next();
            assertEquals("a", first.get(0));
            assertEquals("b", first.get(1));
            assertEquals("c", first.get(2));

            final CSVRecord second = itr1.next();
            assertEquals("d", second.get(0));
            assertEquals("e", second.get(1));
            assertEquals("f", second.get(2));
        }
    }

    @Test
    public void testNewCSVParserNullReaderFormat() {
        assertThrows(NullPointerException.class, () -> new CSVParser(null, CSVFormatPredefinedFormats.Default.getFormat()));
    }

    @Test
    public void testNewCSVParserReaderNullFormat() {
        assertThrows(NullPointerException.class, () -> new CSVParser(new StringReader(""), null));
    }

    @Test
    public void testNoHeaderMap() throws Exception {
        try (final ICSVParser parser = CSVParser.parse("a,b,c\n1,2,3\nx,y,z", CSVFormatPredefinedFormats.Default.getFormat())) {
            assertNull(parser.getHeaderMap());
        }
    }

    @Test
    public void testNotValueCSV() throws IOException {
        final String source = "#";
        final CSVFormat csvFormat = new CSVFormatBuilder().setCommentMarker('#').build();
        final ICSVParser ICSVParser = new CSVParser(new StringReader(source), csvFormat);
        final CSVRecord csvRecord = ICSVParser.nextRecord();
        assertNull(csvRecord);
    }


    @Test
    public void testRoundtrip() throws Exception {
        final StringWriter out = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(out, CSVFormatPredefinedFormats.Default.getFormat())) {
            final String input = "a,b,c\r\n1,2,3\r\nx,y,z\r\n";
            for (final CSVRecord record : CSVParser.parse(input, CSVFormatPredefinedFormats.Default.getFormat())) {
                printer.printRecord(record);
            }
            assertEquals(input, out.toString());
        }
    }

    @Test
    public void testStream() throws Exception {
        final Reader in = new StringReader("a,b,c\n1,2,3\nx,y,z");
        final ICSVParser ICSVParser = new CSVParser(in, CSVFormatPredefinedFormats.Default.getFormat());
        final List<CSVRecord> list = ICSVParser.stream().collect(Collectors.toList());
        assertFalse(list.isEmpty());
        assertArrayEquals(new String[] {"a", "b", "c"}, list.get(0).values());
        assertArrayEquals(new String[] {"1", "2", "3"}, list.get(1).values());
        assertArrayEquals(new String[] {"x", "y", "z"}, list.get(2).values());
    }

    @Test
    public void testTrailingDelimiter() throws Exception {
        final Reader in = new StringReader("a,a,a,\n\"1\",\"2\",\"3\",\nx,y,z,");
        final ICSVParser ICSVParser = new CSVParser(in, new CSVFormatBuilder().setHeaders("X", "Y", "Z").setSkipHeaderRecord(true)
                .setTrailingDelimiter(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertEquals("1", record.get("X"));
        assertEquals("2", record.get("Y"));
        assertEquals("3", record.get("Z"));
        assertEquals(3, record.size());
    }

    @Test
    public void testTrim() throws Exception {
        final Reader in = new StringReader("a,a,a\n\" 1 \",\" 2 \",\" 3 \"\nx,y,z");
        final ICSVParser ICSVParser = new CSVParser(in,  new CSVFormatBuilder().setHeaders("X", "Y", "Z").setSkipHeaderRecord(true)
                .setTrim(true).build());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertEquals("1", record.get("X"));
        assertEquals("2", record.get("Y"));
        assertEquals("3", record.get("Z"));
        assertEquals(3, record.size());
    }

}
