package org.apache.commons.csv.format;

import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static org.apache.commons.csv.Constants.CR;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CSVFormatHeaderTest {

    public enum Header {
        Name, Email, Phone
    }

    @Test
    public void testDuplicateHeaderElements() {
        final String[] header = { "A", "A" };
        final CSVFormat format = new CSVFormatBuilder().setHeaders(header).build();
        assertEquals(2, format.getHeader().length);
        assertArrayEquals(header, format.getHeader());
    }

    @Test
    public void testDuplicateHeaderElementsFalse() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CSVFormatBuilder().setAllowDuplicateHeaderNames(false).setHeaders("A", "A").build());
    }

    @Test
    public void testDuplicateHeaderElementsTrue() {
        new CSVFormatBuilder().setAllowDuplicateHeaderNames(true).setHeaders("A", "A").build();
    }


    @Test
    public void testEqualsHeader() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setRecordSeparator(CR)
                .setCommentMarker('#')
                .setEscape('+')
                .setHeaders("One", "Two", "Three")
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .setQuoteMode(QuoteMode.ALL)
                .build();
        CSVFormat left = right.copy();
        left.setHeader(new String[]{"Three", "Two", "One"});

        assertNotEquals(right, left);
    }

    @Test
    public void testEqualsSkipHeaderRecord() {
        final CSVFormat right = new CSVFormatBuilder().setDelimiter('\'')
                .setRecordSeparator(CR)
                .setCommentMarker('#')
                .setEscape('+')
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .setQuoteMode(QuoteMode.ALL)
                .setNullString("null")
                .setSkipHeaderRecord(true)
                .build();
        CSVFormat left = right.copy();
                left.setSkipHeaderRecord(false);

        assertNotEquals(right, left);
    }


    @Test
    public void testGetHeader() {
        final String[] header = {"one", "two", "three"};
        final CSVFormat formatWithHeader = new CSVFormatBuilder().setHeaders(header).build();
        // getHeader() makes a copy of the header array.
        final String[] headerCopy = formatWithHeader.getHeader();
        headerCopy[0] = "A";
        headerCopy[1] = "B";
        headerCopy[2] = "C";
        assertFalse(Arrays.equals(formatWithHeader.getHeader(), headerCopy));
        assertNotSame(formatWithHeader.getHeader(), headerCopy);
    }

    @Test
    public void testWithHeaderEnum() {
        final CSVFormat formatWithHeader = new CSVFormatBuilder().setHeader(Header.class).build();
        assertArrayEquals(new String[]{ "Name", "Email", "Phone" }, formatWithHeader.getHeader());
    }

    @Test
    public void testWithHeaderEnumInFormatClass() {
        final CSVFormat formatWithHeader = CSVFormatPredefinedFormats.Default.getFormat();
        formatWithHeader.setHeader(Header.class);
        assertArrayEquals(new String[]{ "Name", "Email", "Phone" }, formatWithHeader.getHeader());
    }

    @Test
    public void testWithHeaderResultSetMetadata() throws SQLException {
        final ResultSet resultSet = new SimpleResultSet();
        final CSVFormat formatWithHeader = new CSVFormatBuilder().setHeader(resultSet.getMetaData()).build();
        assertEquals(0, formatWithHeader.getHeader().length);
    }

    @Test
    public void testWithHeaderResultSet() throws SQLException {
        final ResultSet resultSet = new SimpleResultSet();
        final CSVFormat formatWithHeader = new CSVFormatBuilder().setHeader(resultSet).build();
        assertEquals(0, formatWithHeader.getHeader().length);
    }
}
