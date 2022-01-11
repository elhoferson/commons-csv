package org.apache.commons.csv.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JiraCsv106 {

    @Test
    public void testNullRecordSeparatorCsv106() {
        final CSVFormat format = new CSVFormatBuilder().setDelimiter(';').setSkipHeaderRecord(true).setHeaders("H1", "H2").build();
        final String formatStr = format.format("A", "B");
        assertNotNull(formatStr);
        assertFalse(formatStr.endsWith("null"));
    }
}
