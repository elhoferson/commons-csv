package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JiraCsv57Test {

    @Test
    public void testCSV57() throws Exception {
        try (final ICSVParser parser = CSVParser.parse("", CSVFormatPredefinedFormats.Default.getFormat())) {
            final List<CSVRecord> list = parser.getRecords();
            assertNotNull(list);
            assertEquals(0, list.size());
        }
    }
}
