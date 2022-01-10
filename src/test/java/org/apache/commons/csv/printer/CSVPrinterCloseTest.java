package org.apache.commons.csv.printer;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;

import static org.mockito.Mockito.*;

public class CSVPrinterCloseTest {

    @Test
    public void testCloseBackwardCompatibility() throws IOException {
        try (final Writer writer = mock(Writer.class)) {
            final CSVFormat csvFormat = CSVFormatPredefinedFormats.Default.getFormat();
            try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
                // empty
            }
            verify(writer, never()).flush();
            verify(writer, times(1)).close();
        }}

    @Test
    public void testCloseWithCsvFormatAutoFlushOff() throws IOException {
        try (final Writer writer = mock(Writer.class)) {
            CSVFormat csvFormat = CSVFormatPredefinedFormats.Default.getFormat();
            csvFormat.setAutoFlush(false);
            try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
                // empty
            }
            verify(writer, never()).flush();
            verify(writer, times(1)).close();
        }
    }

    @Test
    public void testCloseWithCsvFormatAutoFlushOn() throws IOException {
        // System.out.println("start method");
        try (final Writer writer = mock(Writer.class)) {
            final CSVFormat csvFormat = CSVFormatPredefinedFormats.Default.getFormat();
            csvFormat.setAutoFlush(true);
            try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
                // empty
            }
            verify(writer, times(1)).flush();
            verify(writer, times(1)).close();
        }}

    @Test
    public void testCloseWithFlushOff() throws IOException {
        try (final Writer writer = mock(Writer.class)) {
            final CSVFormat csvFormat = CSVFormatPredefinedFormats.Default.getFormat();
            @SuppressWarnings("resource")
            final CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
            csvPrinter.close(false);
            verify(writer, never()).flush();
            verify(writer, times(1)).close();
        }
    }

    @Test
    public void testCloseWithFlushOn() throws IOException {
        try (final Writer writer = mock(Writer.class)) {
            @SuppressWarnings("resource")
            final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormatPredefinedFormats.Default.getFormat());
            csvPrinter.close(true);
            verify(writer, times(1)).flush();
        }
    }
}
