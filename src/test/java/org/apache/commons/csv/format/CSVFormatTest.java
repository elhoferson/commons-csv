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

package org.apache.commons.csv.format;

import org.apache.commons.csv.Constants;
import org.apache.commons.csv.printer.CSVPrinter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;

import static org.apache.commons.csv.Constants.CRLF;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link CSVFormat}.
 */
public class CSVFormatTest {

    @Test
    public void testFormat() {
        final CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();

        assertEquals("", format.format());
        assertEquals("a,b,c", format.format("a", "b", "c"));
        assertEquals("\"x,y\",z", format.format("x,y", "z"));
    }

    @Test  //I assume this to be a defect.
    public void testFormatThrowsNullPointerException() {

        final CSVFormat csvFormat = CSVFormatPredefinedFormats.MySQL.getFormat();

        final NullPointerException e = assertThrows(NullPointerException.class, () -> csvFormat.format((Object[]) null));
        assertEquals(Objects.class.getName(), e.getStackTrace()[0].getClassName());
    }

    @Test
    public void testFormatToString() {
        CSVFormat format = CSVFormatPredefinedFormats.RFC4180.getFormat();
        format.setEscapeCharacter('?');
        format.setQuoteMode(QuoteMode.MINIMAL);
        format.setDelimiter(",");
        format.setQuoteCharacter('"');
        format.setRecordSeparator(CRLF);
        format.setNullString("");
        format.setIgnoreHeaderCase(true);
        format.setHeaderComments("This is HeaderComments");
        format.setHeader("col1","col2","col3");

        assertEquals("Delimiter=<,> Escape=<?> QuoteChar=<\"> QuoteMode=<MINIMAL> NullString=<> RecordSeparator=<" +CRLF+
                "> IgnoreHeaderCase:ignored SkipHeaderRecord:false HeaderComments:[This is HeaderComments] Header:[col1, col2, col3]", format.toString());
    }

    @Test
    public void testHashCodeAndWithIgnoreHeaderCase() {

        final CSVFormat csvFormat = CSVFormatPredefinedFormats.InformixUnloadCsv.getFormat();
        final CSVFormat csvFormatTwo = csvFormat.copy();
        csvFormatTwo.setIgnoreHeaderCase(true);
        csvFormatTwo.hashCode();

        assertFalse(csvFormat.getIgnoreHeaderCase());
        assertTrue(csvFormatTwo.getIgnoreHeaderCase()); // now different
        assertFalse(csvFormatTwo.getTrailingDelimiter());

        Assertions.assertNotEquals(csvFormatTwo, csvFormat); // CSV-244 - should not be equal
        assertFalse(csvFormatTwo.getAllowMissingColumnNames());

        assertFalse(csvFormatTwo.getTrim());

    }

    @Test
    public void testNewFormat() {

        final CSVFormat csvFormat = CSVFormat.newFormat('X');

        assertFalse(csvFormat.getSkipHeaderRecord());
        assertFalse(csvFormat.isEscapeCharacterSet());

        assertNull(csvFormat.getRecordSeparator());
        assertNull(csvFormat.getQuoteMode());

        assertNull(csvFormat.getCommentMarker());
        assertFalse(csvFormat.getIgnoreHeaderCase());

        assertFalse(csvFormat.getAllowMissingColumnNames());
        assertFalse(csvFormat.getTrim());

        assertFalse(csvFormat.isNullStringSet());
        assertNull(csvFormat.getEscapeCharacter());

        assertFalse(csvFormat.getIgnoreSurroundingSpaces());
        assertFalse(csvFormat.getTrailingDelimiter());

        assertEquals("X", csvFormat.getDelimiterString());
        assertNull(csvFormat.getNullString());

        assertFalse(csvFormat.isQuoteCharacterSet());
        assertFalse(csvFormat.isCommentMarkerSet());

        assertNull(csvFormat.getQuoteCharacter());
        assertFalse(csvFormat.getIgnoreEmptyLines());

        assertFalse(csvFormat.getSkipHeaderRecord());
        assertFalse(csvFormat.isEscapeCharacterSet());

        assertNull(csvFormat.getRecordSeparator());
        assertNull(csvFormat.getQuoteMode());

        assertNull(csvFormat.getCommentMarker());
        assertFalse(csvFormat.getIgnoreHeaderCase());

        assertFalse(csvFormat.getAllowMissingColumnNames());
        assertFalse(csvFormat.getTrim());

        assertFalse(csvFormat.isNullStringSet());
        assertNull(csvFormat.getEscapeCharacter());

        assertFalse(csvFormat.getIgnoreSurroundingSpaces());
        assertFalse(csvFormat.getTrailingDelimiter());

        assertEquals("X", csvFormat.getDelimiterString());
        assertNull(csvFormat.getNullString());

        assertFalse(csvFormat.isQuoteCharacterSet());
        assertFalse(csvFormat.isCommentMarkerSet());

        assertNull(csvFormat.getQuoteCharacter());
        assertFalse(csvFormat.getIgnoreEmptyLines());

    }

    @Test
    public void testPrintWithEscapesEndWithCRLF() throws IOException {
        final Reader in = new StringReader("x,y,x\r\na,?b,c\r\n");
        final Appendable out = new StringBuilder();
        CSVFormat format = CSVFormatPredefinedFormats.RFC4180.getFormat();
        format.setEscapeCharacter('?');
        format.setDelimiter(",");
        format.setQuoteCharacter(null);
        format.setRecordSeparator(CRLF);
        CSVPrinter csvPrinter = new CSVPrinter(out, format);
        csvPrinter.print(in,  true);
        assertEquals("x?,y?,x?r?na?,??b?,c?r?n", out.toString());
    }

    @Test
    public void testPrintWithEscapesEndWithoutCRLF() throws IOException {
        final Reader in = new StringReader("x,y,x");
        final Appendable out = new StringBuilder();
        CSVFormat format = CSVFormatPredefinedFormats.RFC4180.getFormat();
        format.setEscapeCharacter('?');
        format.setDelimiter(",");
        format.setQuoteCharacter(null);
        format.setRecordSeparator(CRLF);
        CSVPrinter csvPrinter = new CSVPrinter(out, format);
        csvPrinter.print(in, true);
        assertEquals("x?,y?,x", out.toString());
    }

    @Test
    public void testRFC4180() {

        final CSVFormat format = CSVFormatPredefinedFormats.RFC4180.getFormat();
        assertNull(format.getCommentMarker());
        assertEquals(",", format.getDelimiterString());
        assertNull(format.getEscapeCharacter());
        assertFalse(format.getIgnoreEmptyLines());
        assertEquals(Character.valueOf('"'), format.getQuoteCharacter());
        assertNull(format.getQuoteMode());
        assertEquals("\r\n", format.getRecordSeparator());
    }

    @SuppressWarnings("boxing") // no need to worry about boxing here
    @Test
    public void testSerialization() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (final ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(CSVFormatPredefinedFormats.Default.getFormat());
            oos.flush();
        }

        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        final CSVFormat format = (CSVFormat) in.readObject();

        assertNotNull(format);
        final CSVFormat targetFormat = CSVFormatPredefinedFormats.Default.getFormat();
        assertEquals(targetFormat.getDelimiterString(), format.getDelimiterString(), "delimiter");
        assertEquals(targetFormat.getQuoteCharacter(), format.getQuoteCharacter(), "encapsulator");
        assertEquals(targetFormat.getCommentMarker(), format.getCommentMarker(), "comment start");
        assertEquals(targetFormat.getRecordSeparator(), format.getRecordSeparator(), "record separator");
        assertEquals(targetFormat.getEscapeCharacter(), format.getEscapeCharacter(), "escape");
        assertEquals(targetFormat.getIgnoreSurroundingSpaces(), format.getIgnoreSurroundingSpaces(), "trim");
        assertEquals(targetFormat.getIgnoreEmptyLines(), format.getIgnoreEmptyLines(), "empty lines");
    }

    @Test
    public void testToString() {

        final String string = CSVFormatPredefinedFormats.InformixUnload.getFormat().toString();

        assertEquals("Delimiter=<|> Escape=<\\> QuoteChar=<\"> RecordSeparator=<\n> EmptyLines:ignored SkipHeaderRecord:false", string);

    }

    @Test
    public void testToStringAndWithCommentMarkerTakingCharacter() {

        final CSVFormatPredefinedFormats csvFormat_CSVFormat_PredefinedFormats = CSVFormatPredefinedFormats.Default;
        final CSVFormat csvFormat = csvFormat_CSVFormat_PredefinedFormats.getFormat();

        assertNull(csvFormat.getEscapeCharacter());
        assertTrue(csvFormat.isQuoteCharacterSet());

        assertFalse(csvFormat.getTrim());
        assertFalse(csvFormat.getIgnoreSurroundingSpaces());

        assertFalse(csvFormat.getTrailingDelimiter());
        assertEquals(",", csvFormat.getDelimiterString());

        assertFalse(csvFormat.getIgnoreHeaderCase());
        assertEquals("\r\n", csvFormat.getRecordSeparator());

        assertFalse(csvFormat.isCommentMarkerSet());
        assertNull(csvFormat.getCommentMarker());

        assertFalse(csvFormat.isNullStringSet());
        assertFalse(csvFormat.getAllowMissingColumnNames());

        assertFalse(csvFormat.isEscapeCharacterSet());
        assertFalse(csvFormat.getSkipHeaderRecord());

        assertNull(csvFormat.getNullString());
        assertNull(csvFormat.getQuoteMode());

        assertTrue(csvFormat.getIgnoreEmptyLines());
        assertEquals('\"', (char)csvFormat.getQuoteCharacter());

        final Character character = Character.valueOf('n');
        final CSVFormat csvFormatTwo = csvFormat.copy();
        csvFormatTwo.setCommentMarker(character);

        assertNull(csvFormat.getEscapeCharacter());
        assertTrue(csvFormat.isQuoteCharacterSet());

        assertFalse(csvFormat.getTrim());
        assertFalse(csvFormat.getIgnoreSurroundingSpaces());

        assertFalse(csvFormat.getTrailingDelimiter());
        assertEquals(",", csvFormat.getDelimiterString());

        assertFalse(csvFormat.getIgnoreHeaderCase());
        assertEquals("\r\n", csvFormat.getRecordSeparator());

        assertFalse(csvFormat.isCommentMarkerSet());
        assertNull(csvFormat.getCommentMarker());

        assertFalse(csvFormat.isNullStringSet());
        assertFalse(csvFormat.getAllowMissingColumnNames());

        assertFalse(csvFormat.isEscapeCharacterSet());
        assertFalse(csvFormat.getSkipHeaderRecord());

        assertNull(csvFormat.getNullString());
        assertNull(csvFormat.getQuoteMode());

        assertTrue(csvFormat.getIgnoreEmptyLines());
        assertEquals('\"', (char)csvFormat.getQuoteCharacter());

        assertFalse(csvFormatTwo.isNullStringSet());
        assertFalse(csvFormatTwo.getAllowMissingColumnNames());

        assertEquals('\"', (char)csvFormatTwo.getQuoteCharacter());
        assertNull(csvFormatTwo.getNullString());

        assertEquals(",", csvFormatTwo.getDelimiterString());
        assertFalse(csvFormatTwo.getTrailingDelimiter());

        assertTrue(csvFormatTwo.isCommentMarkerSet());
        assertFalse(csvFormatTwo.getIgnoreHeaderCase());

        assertFalse(csvFormatTwo.getTrim());
        assertNull(csvFormatTwo.getEscapeCharacter());

        assertTrue(csvFormatTwo.isQuoteCharacterSet());
        assertFalse(csvFormatTwo.getIgnoreSurroundingSpaces());

        assertEquals("\r\n", csvFormatTwo.getRecordSeparator());
        assertNull(csvFormatTwo.getQuoteMode());

        assertEquals('n', (char)csvFormatTwo.getCommentMarker());
        assertFalse(csvFormatTwo.getSkipHeaderRecord());

        assertFalse(csvFormatTwo.isEscapeCharacterSet());
        assertTrue(csvFormatTwo.getIgnoreEmptyLines());

        assertNotSame(csvFormat, csvFormatTwo);
        assertNotSame(csvFormatTwo, csvFormat);

        Assertions.assertNotEquals(csvFormat, csvFormatTwo);

        assertEquals("Delimiter=<,> QuoteChar=<\"> CommentStart=<n> " +
                        "RecordSeparator=<\r\n> EmptyLines:ignored SkipHeaderRecord:false"
                , csvFormatTwo.toString());

    }

    @Test
    public void testTrim() throws IOException {
        CSVFormat formatWithTrim = CSVFormatPredefinedFormats.Default.getFormat();
        formatWithTrim.setDelimiter(",");
        formatWithTrim.setQuoteCharacter(null);
        formatWithTrim.setRecordSeparator(CRLF);
        formatWithTrim.setTrim(true);

        CharSequence in = "a,b,c";
        final StringBuilder out = new StringBuilder();
        CSVPrinter csvPrinter = new CSVPrinter(out, formatWithTrim);
        csvPrinter.print(in, true);
        assertEquals("a,b,c", out.toString());

        in = new StringBuilder(" x,y,z");
        out.setLength(0);
        csvPrinter.print(in, true);
        assertEquals("x,y,z", out.toString());

        in = new StringBuilder("");
        out.setLength(0);
        csvPrinter.print(in, true);
        assertEquals("", out.toString());

        in = new StringBuilder("header\r\n");
        out.setLength(0);
        csvPrinter.print(in, true);
        assertEquals("header", out.toString());
    }

    @Test
    public void testConstantsInitialization() {
        Constants constants = new Constants();
        assertNotNull(constants);
    }
}
