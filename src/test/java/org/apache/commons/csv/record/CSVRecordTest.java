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
package org.apache.commons.csv.record;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.parser.CSVParser;
import org.apache.commons.csv.parser.ICSVParser;
import org.apache.commons.csv.printer.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CSVRecordTest {

    private enum EnumFixture {
        UNKNOWN_COLUMN
    }

    public enum EnumHeader {
        FIRST("first"),
        SECOND("second"),
        THIRD("third");

        private final String number;

        EnumHeader(final String number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return number;
        }
    }

    private Map<String, Integer> headerMap;
    private CSVRecord record, recordWithHeader;
    private String[] values;

    @BeforeEach
    public void setUp() throws Exception {
        values = new String[] { "A", "B", "C" };
        final String rowData = StringUtils.join(values, ',');
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        try (final ICSVParser parser = new CSVParser(new StringReader(rowData), format)) {
            record = parser.iterator().next();
        }
        final String[] headers = { "first", "second", "third" };
        CSVFormat format1 = format.copy();
        format1.setHeader(headers);
        try (final ICSVParser parser = new CSVParser(new StringReader(rowData), format1)) {
            recordWithHeader = parser.iterator().next();
            headerMap = parser.getHeaderMap();
        }
    }

    @Test
    public void testCSVRecordNULLValues() throws IOException {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setHeader();
        final ICSVParser parser = CSVParser.parse("A,B\r\nONE,TWO", format);
        final CSVRecord csvRecord = new CSVRecord(parser, null, null, 0L, 0L);
        assertEquals(0, csvRecord.size());
        assertThrows(IllegalArgumentException.class, () -> csvRecord.get("B"));
    }

    @Test
    public void testGetInt() {
        assertEquals(values[0], record.get(0));
        assertEquals(values[1], record.get(1));
        assertEquals(values[2], record.get(2));
    }

    @Test
    public void testGetNullEnum() {
        assertThrows(IllegalArgumentException.class, () -> recordWithHeader.get((Enum<?>) null));
    }

    @Test
    public void testGetString() {
        assertEquals(values[0], recordWithHeader.get("first"));
        assertEquals(values[1], recordWithHeader.get("second"));
        assertEquals(values[2], recordWithHeader.get("third"));
    }

    @Test
    public void testGetStringInconsistentRecord() {
        headerMap.put("fourth", Integer.valueOf(4));
        assertThrows(IllegalArgumentException.class, () -> recordWithHeader.get("fourth"));
    }

    @Test
    public void testGetStringNoHeader() {
        assertThrows(IllegalStateException.class, () -> record.get("first"));
    }

    @Test
    public void testGetUnmappedEnum() {
        assertThrows(IllegalArgumentException.class, () -> recordWithHeader.get(EnumFixture.UNKNOWN_COLUMN));
    }

    @Test
    public void testGetUnmappedName() {
        assertThrows(IllegalArgumentException.class, () -> assertNull(recordWithHeader.get("fourth")));
    }

    @Test
    public void testGetUnmappedNegativeInt() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> recordWithHeader.get(Integer.MIN_VALUE));
    }

    @Test
    public void testGetUnmappedPositiveInt() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> recordWithHeader.get(Integer.MAX_VALUE));
    }

    @Test
    public void testGetWithEnum() {
        assertEquals(recordWithHeader.get("first"), recordWithHeader.get(EnumHeader.FIRST));
        assertEquals(recordWithHeader.get("second"), recordWithHeader.get(EnumHeader.SECOND));
        assertThrows(IllegalArgumentException.class, () -> recordWithHeader.get(EnumFixture.UNKNOWN_COLUMN));
    }

    @Test
    public void testIsConsistent() {
        assertTrue(record.isConsistent());
        assertTrue(recordWithHeader.isConsistent());
        final Map<String, Integer> map = recordWithHeader.getParser().getHeaderMap();
        map.put("fourth", Integer.valueOf(4));
        // We are working on a copy of the map, so the record should still be OK.
        assertTrue(recordWithHeader.isConsistent());
    }

    @Test
    public void testIsInconsistent() throws IOException {
        final String[] headers = { "first", "second", "third" };
        final String rowData = StringUtils.join(values, ',');
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setHeader(headers);
        try (final ICSVParser parser = new CSVParser(new StringReader(rowData), format)) {
            final Map<String, Integer> map = parser.getHeaderMapRaw();
            final CSVRecord record1 = parser.iterator().next();
            map.put("fourth", Integer.valueOf(4));
            assertFalse(record1.isConsistent());
        }
    }

    @Test
    public void testIsMapped() {
        assertFalse(record.isMapped("first"));
        assertTrue(recordWithHeader.isMapped("first"));
        assertFalse(recordWithHeader.isMapped("fourth"));
    }

    @Test
    public void testIsSetInt() {
        assertFalse(record.isSet(-1));
        assertTrue(record.isSet(0));
        assertTrue(record.isSet(2));
        assertFalse(record.isSet(3));
        assertTrue(recordWithHeader.isSet(1));
        assertFalse(recordWithHeader.isSet(1000));
    }

    @Test
    public void testIsSetString() {
        assertFalse(record.isSet("first"));
        assertTrue(recordWithHeader.isSet("first"));
        assertFalse(recordWithHeader.isSet("fourth"));
    }

    @Test
    public void testIterator() {
        int i = 0;
        for (final String value : record) {
            assertEquals(values[i], value);
            i++;
        }
    }

    @Test
    public void testPutInMap() {
        final Map<String, String> map = new ConcurrentHashMap<>();
        this.recordWithHeader.putIn(map);
        this.validateMap(map, false);
        // Test that we can compile with assignment to the same map as the param.
        final TreeMap<String, String> map2 = recordWithHeader.putIn(new TreeMap<>());
        this.validateMap(map2, false);
    }

    @Test
    public void testRemoveAndAddColumns() throws IOException {
        // do:
        try (final CSVPrinter printer = new CSVPrinter(new StringBuilder(), CSVFormatPredefinedFormats.Default.getFormat())) {
            final Map<String, String> map = recordWithHeader.toMap();
            map.remove("OldColumn");
            map.put("ZColumn", "NewValue");
            // check:
            final ArrayList<String> list = new ArrayList<>(map.values());
            Collections.sort(list);
            printer.printRecord(list);
            assertEquals("A,B,C,NewValue" + CSVFormatPredefinedFormats.Default.getFormat().getRecordSeparator(), printer.getOut().toString());
        }
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final CSVRecord shortRec;
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setHeader();
        format.setCommentMarker('#');
        try (final ICSVParser parser = CSVParser.parse("A,B\n#my comment\nOne,Two", format)) {
            shortRec = parser.iterator().next();
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(shortRec);
        }
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            final Object object = ois.readObject();
            assertTrue(object instanceof CSVRecord);
            final CSVRecord rec = (CSVRecord) object;
            assertEquals(1L, rec.getRecordNumber());
            assertEquals("One", rec.get(0));
            assertEquals("Two", rec.get(1));
            assertEquals(2, rec.size());
            assertEquals(shortRec.getCharacterPosition(), rec.getCharacterPosition());
            assertEquals("my comment", rec.getComment());
            // The parser is not serialized
            assertNull(rec.getParser());
            // Check all header map functionality is absent
            assertTrue(rec.isConsistent());
            assertFalse(rec.isMapped("A"));
            assertFalse(rec.isSet("A"));
            assertEquals(0, rec.toMap().size());
            // This will throw
            try {
                rec.get("A");
                org.junit.jupiter.api.Assertions.fail("Access by name is not expected after deserialisation");
            } catch (final IllegalStateException expected) {
                // OK
            }
        }
    }

    @Test
    public void testStream() {
        final AtomicInteger i = new AtomicInteger();
        record.stream().forEach(value -> {
            assertEquals(values[i.get()], value);
            i.incrementAndGet();
        });
    }

    @Test
    public void testToList() {
        int i = 0;
        for (final String value : record.toList()) {
            assertEquals(values[i], value);
            i++;
        }
    }

    @Test
    public void testToMap() {
        final Map<String, String> map = this.recordWithHeader.toMap();
        this.validateMap(map, true);
    }

    @Test
    public void testToMapWithNoHeader() throws Exception {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        try (final ICSVParser parser = CSVParser.parse("a,b", format)) {
            final CSVRecord shortRec = parser.iterator().next();
            final Map<String, String> map = shortRec.toMap();
            assertNotNull(map, "Map is not null.");
            assertTrue(map.isEmpty(), "Map is empty.");
        }
    }

    @Test
    public void testToMapWithShortRecord() throws Exception {
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setHeader("A", "B", "C");
        try (final ICSVParser parser = CSVParser.parse("a,b", format)) {
            final CSVRecord shortRec = parser.iterator().next();
            shortRec.toMap();
        }
    }

    @Test
    public void testToString() {
        assertNotNull(recordWithHeader.toString());
        assertTrue(recordWithHeader.toString().contains("comment="));
        assertTrue(recordWithHeader.toString().contains("recordNumber="));
        assertTrue(recordWithHeader.toString().contains("values="));
    }

    private void validateMap(final Map<String, String> map, final boolean allowsNulls) {
        assertTrue(map.containsKey("first"));
        assertTrue(map.containsKey("second"));
        assertTrue(map.containsKey("third"));
        assertFalse(map.containsKey("fourth"));
        if (allowsNulls) {
            assertFalse(map.containsKey(null));
        }
        assertEquals("A", map.get("first"));
        assertEquals("B", map.get("second"));
        assertEquals("C", map.get("third"));
        assertNull(map.get("fourth"));
    }
}
