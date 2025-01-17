package org.apache.commons.csv.printer;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import static org.apache.commons.csv.Constants.BACKSLASH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JiraCsv259Test {

    @Test
    public void testCSV259() throws IOException {
        final StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
        format.setQuoteCharacter(null);
        format.setEscapeCharacter('!');
        try (final Reader reader = new FileReader("src/test/resources/org/apache/commons/csv/CSV-259/sample.txt");
             final CSVPrinter printer = new CSVPrinter(sw, format)) {
            printer.print(reader);
            assertEquals("x!,y!,z", sw.toString());
        }
    }
}
