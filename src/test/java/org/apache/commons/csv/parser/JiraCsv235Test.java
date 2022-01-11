package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JiraCsv235Test {


    @Test
    public void testCSV235() throws IOException {
        final String dqString = "\"aaa\",\"b\"\"bb\",\"ccc\""; // "aaa","b""bb","ccc"
        final ICSVParser ICSVParser = new CSVParser(new StringReader(dqString), CSVFormatPredefinedFormats.RFC4180.getFormat());
        final Iterator<CSVRecord> records = ICSVParser.iterator();
        final CSVRecord record = records.next();
        assertFalse(records.hasNext());
        assertEquals(3, record.size());
        assertEquals("aaa", record.get(0));
        assertEquals("b\"bb", record.get(1));
        assertEquals("ccc", record.get(2));
    }
}
