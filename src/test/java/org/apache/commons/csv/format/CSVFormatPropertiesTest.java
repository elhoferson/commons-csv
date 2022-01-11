package org.apache.commons.csv.format;

import org.apache.commons.csv.enums.EmptyEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.commons.csv.Constants.*;
import static org.apache.commons.csv.Constants.LF;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVFormatPropertiesTest {

    @Test
    public void testWithCommentStart() {
        final CSVFormat formatWithCommentStart = new CSVFormatBuilder().setCommentMarker('#').build();
        assertEquals( Character.valueOf('#'), formatWithCommentStart.getCommentMarker());
    }


    @Test
    public void testWithCommentStartCRThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setCommentMarker(CR).build());
    }


    @Test
    public void testWithDelimiter() {
        final CSVFormat formatWithDelimiter = new CSVFormatBuilder().setDelimiter('!').build();
        assertEquals("!", formatWithDelimiter.getDelimiterString());
    }


    @Test
    public void testWithDelimiterLFThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setDelimiter(LF).build());
    }


    @Test
    public void testWithEmptyEnum() {
        final CSVFormat formatWithHeader = new CSVFormatBuilder().setHeader(EmptyEnum.class).build();
        assertEquals(0, formatWithHeader.getHeader().length);
    }


    @Test
    public void testWithEscape() {
        final CSVFormat formatWithEscape = new CSVFormatBuilder().setEscape('&').build();
        assertEquals(Character.valueOf('&'), formatWithEscape.getEscapeCharacter());
    }


    @Test
    public void testWithEscapeCRThrowsExceptions() {
        assertThrows(IllegalArgumentException.class, () -> new CSVFormatBuilder().setEscape(CR).build());
    }


    @Test
    public void testWithFirstRecordAsHeader() {
        final CSVFormat formatWithFirstRecordAsHeader = new CSVFormatBuilder().setHeaders()
                .setSkipHeaderRecord(true)
                .build();
        assertTrue(formatWithFirstRecordAsHeader.getSkipHeaderRecord());
        assertEquals(0, formatWithFirstRecordAsHeader.getHeader().length);
    }

    @Test
    public void testWithHeader() {
        final String[] header = {"one", "two", "three"};
        // withHeader() makes a copy of the header array.
        final CSVFormat formatWithHeader = new CSVFormatBuilder().setHeader(header).build();
        assertArrayEquals(header, formatWithHeader.getHeader());
        assertNotSame(header, formatWithHeader.getHeader());
    }

    @Test
    public void testWithHeaderComments() {

        final CSVFormat csvFormat = new CSVFormatBuilder().build();

        assertEquals('\"', (char)csvFormat.getQuoteCharacter());
        assertFalse(csvFormat.isCommentMarkerSet());

        assertFalse(csvFormat.isEscapeCharacterSet());
        assertTrue(csvFormat.isQuoteCharacterSet());

        assertFalse(csvFormat.getSkipHeaderRecord());
        assertNull(csvFormat.getQuoteMode());

        assertEquals(",", csvFormat.getDelimiterString());
        assertTrue(csvFormat.getIgnoreEmptyLines());

        assertFalse(csvFormat.getIgnoreHeaderCase());
        assertNull(csvFormat.getCommentMarker());

        assertEquals("\r\n", csvFormat.getRecordSeparator());
        assertFalse(csvFormat.getTrailingDelimiter());

        assertFalse(csvFormat.getAllowMissingColumnNames());
        assertFalse(csvFormat.getTrim());

        assertFalse(csvFormat.isNullStringSet());
        assertNull(csvFormat.getNullString());

        assertFalse(csvFormat.getIgnoreSurroundingSpaces());
        assertNull(csvFormat.getEscapeCharacter());

        final String[] objectArray = new String[8];
        CSVFormat csvFormatTwo = csvFormat.copy();
        csvFormatTwo.setHeaderComments(objectArray);

        assertEquals('\"', (char)csvFormat.getQuoteCharacter());
        assertFalse(csvFormat.isCommentMarkerSet());

        assertFalse(csvFormat.isEscapeCharacterSet());
        assertTrue(csvFormat.isQuoteCharacterSet());

        assertFalse(csvFormat.getSkipHeaderRecord());
        assertNull(csvFormat.getQuoteMode());

        assertEquals(",", csvFormat.getDelimiterString());
        assertTrue(csvFormat.getIgnoreEmptyLines());

        assertFalse(csvFormat.getIgnoreHeaderCase());
        assertNull(csvFormat.getCommentMarker());

        assertEquals("\r\n", csvFormat.getRecordSeparator());
        assertFalse(csvFormat.getTrailingDelimiter());

        assertFalse(csvFormat.getAllowMissingColumnNames());
        assertFalse(csvFormat.getTrim());

        assertFalse(csvFormat.isNullStringSet());
        assertNull(csvFormat.getNullString());

        assertFalse(csvFormat.getIgnoreSurroundingSpaces());
        assertNull(csvFormat.getEscapeCharacter());

        assertFalse(csvFormatTwo.getIgnoreHeaderCase());
        assertNull(csvFormatTwo.getQuoteMode());

        assertTrue(csvFormatTwo.getIgnoreEmptyLines());
        assertFalse(csvFormatTwo.getIgnoreSurroundingSpaces());

        assertNull(csvFormatTwo.getEscapeCharacter());
        assertFalse(csvFormatTwo.getTrim());

        assertFalse(csvFormatTwo.isEscapeCharacterSet());
        assertTrue(csvFormatTwo.isQuoteCharacterSet());

        assertFalse(csvFormatTwo.getSkipHeaderRecord());
        assertEquals('\"', (char)csvFormatTwo.getQuoteCharacter());

        assertFalse(csvFormatTwo.getAllowMissingColumnNames());
        assertNull(csvFormatTwo.getNullString());

        assertFalse(csvFormatTwo.isNullStringSet());
        assertFalse(csvFormatTwo.getTrailingDelimiter());

        assertEquals("\r\n", csvFormatTwo.getRecordSeparator());
        assertEquals(",", csvFormatTwo.getDelimiterString());

        assertNull(csvFormatTwo.getCommentMarker());
        assertFalse(csvFormatTwo.isCommentMarkerSet());

        assertNotSame(csvFormat, csvFormatTwo);
        assertNotSame(csvFormatTwo, csvFormat);

        Assertions.assertNotEquals(csvFormatTwo, csvFormat); // CSV-244 - should not be equal

        final String string = csvFormatTwo.format(objectArray);

        assertNotNull(string);
        Assertions.assertNotEquals(csvFormat, csvFormatTwo); // CSV-244 - should not be equal

        Assertions.assertNotEquals(csvFormatTwo, csvFormat); // CSV-244 - should not be equal
        assertEquals(",,,,,,,", string);

    }

    @Test
    public void testWithHeaderEnumNull() {
        final Class<Enum<?>> simpleName = null;
        final CSVFormat format =  new CSVFormatBuilder().setHeader(simpleName).build();
    }

    @Test
    public  void testWithHeaderResultSetNull() throws SQLException {
        final ResultSet resultSet = null;
        final CSVFormat format = new CSVFormatBuilder().setHeader(resultSet).build();
    }

    @Test
    public void testWithIgnoreEmptyLines() {
        assertFalse(new CSVFormatBuilder().setIgnoreEmptyLines(false).build().getIgnoreEmptyLines());
        assertTrue(new CSVFormatBuilder().setIgnoreEmptyLines(true).build().getIgnoreEmptyLines());
    }

    @Test
    public void testWithIgnoreSurround() {
        assertFalse(new CSVFormatBuilder().setIgnoreSurroundingSpaces(false).build().getIgnoreSurroundingSpaces());
        assertTrue(new CSVFormatBuilder().setIgnoreSurroundingSpaces(true).build().getIgnoreSurroundingSpaces());
    }

    @Test
    public void testWithNullString() {
        final CSVFormat formatWithNullString = new CSVFormatBuilder().setNullString("null").build();
        assertEquals("null", formatWithNullString.getNullString());
    }

    @Test
    public void testWithRecordSeparatorCR() {
        final CSVFormat formatWithRecordSeparator = new CSVFormatBuilder().setRecordSeparator(CR).build();
        assertEquals(String.valueOf(CR), formatWithRecordSeparator.getRecordSeparator());
    }

    @Test
    public void testWithRecordSeparatorCRLF() {
        final CSVFormat formatWithRecordSeparator = new CSVFormatBuilder().setRecordSeparator(CRLF).build();
        assertEquals(CRLF, formatWithRecordSeparator.getRecordSeparator());
    }

    @Test
    public void testWithRecordSeparatorLF() {
        final CSVFormat formatWithRecordSeparator = new CSVFormatBuilder().setRecordSeparator(LF).build();
        assertEquals(String.valueOf(LF), formatWithRecordSeparator.getRecordSeparator());
    }

    @Test
    public void testWithSystemRecordSeparator() {
        final CSVFormat formatWithRecordSeparator = new CSVFormatBuilder().setRecordSeparator(System.lineSeparator()).build();
        assertEquals(System.getProperty("line.separator"), formatWithRecordSeparator.getRecordSeparator());
    }
}
