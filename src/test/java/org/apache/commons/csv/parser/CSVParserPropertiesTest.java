package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatBuilder;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class CSVParserPropertiesTest {


    private void parseFully(final ICSVParser parser) {
        for (final CSVRecord csvRecord : parser) {
            assertNotNull(csvRecord);
        }
    }

    @Test
    public void testParse() throws Exception {
        final ClassLoader loader = ClassLoader.getSystemClassLoader();
        final URL url = loader.getResource("org/apache/commons/csv/CSVFileParser/test.csv");
        final CSVFormat format = new CSVFormatBuilder().setHeaders("A", "B", "C", "D").build();
        final Charset charset = StandardCharsets.UTF_8;

        try (final ICSVParser parser = CSVParser.parse(new InputStreamReader(url.openStream(), charset), format)) {
            parseFully(parser);
        }
        try (final ICSVParser parser = CSVParser.parse(new String(Files.readAllBytes(Paths.get(url.toURI())), charset),
                format)) {
            parseFully(parser);
        }
        try (final ICSVParser parser = CSVParser.parse(new File(url.toURI()), charset, format)) {
            parseFully(parser);
        }
        try (final ICSVParser parser = CSVParser.parse(url.openStream(), charset, format)) {
            parseFully(parser);
        }
        try (final ICSVParser parser = CSVParser.parse(Paths.get(url.toURI()), charset, format)) {
            parseFully(parser);
        }
        try (final ICSVParser parser = CSVParser.parse(url, charset, format)) {
            parseFully(parser);
        }
        try (final ICSVParser parser = new CSVParser(new InputStreamReader(url.openStream(), charset), format)) {
            parseFully(parser);
        }
        try (final ICSVParser parser = new CSVParser(new InputStreamReader(url.openStream(), charset), format,
                /* characterOffset= */0, /* recordNumber= */1)) {
            parseFully(parser);
        }
    }

    @Test
    public void testParseFileNullFormat() {
        assertThrows(NullPointerException.class,
                () -> CSVParser.parse(new File("CSVFileParser/test.csv"), Charset.defaultCharset(), null));
    }

    @Test
    public void testParseNullFileFormat() {
        assertThrows(NullPointerException.class,
                () -> CSVParser.parse((File) null, Charset.defaultCharset(), CSVFormatPredefinedFormats.Default.getFormat()));
    }

    @Test
    public void testParseNullPathFormat() {
        assertThrows(NullPointerException.class,
                () -> CSVParser.parse((Path) null, Charset.defaultCharset(), CSVFormatPredefinedFormats.Default.getFormat()));
    }

    @Test
    public void testParseNullStringFormat() {
        assertThrows(NullPointerException.class, () -> CSVParser.parse((String) null, CSVFormatPredefinedFormats.Default.getFormat()));
    }

    @Test
    public void testParseNullUrlCharsetFormat() {
        assertThrows(NullPointerException.class,
                () -> CSVParser.parse((URL) null, Charset.defaultCharset(), CSVFormatPredefinedFormats.Default.getFormat()));
    }

    @Test
    public void testParserUrlNullCharsetFormat() {
        assertThrows(NullPointerException.class,
                () -> CSVParser.parse(new URL("https://commons.apache.org"), null, CSVFormatPredefinedFormats.Default.getFormat()));
    }

    @Test
    public void testParseStringNullFormat() {
        assertThrows(NullPointerException.class, () -> CSVParser.parse("csv data", (CSVFormat) null));
    }

    @Test
    public void testParseUrlCharsetNullFormat() {
        assertThrows(NullPointerException.class,
                () -> CSVParser.parse(new URL("https://commons.apache.org"), Charset.defaultCharset(), null));
    }

    @Test
    public void testParseWithDelimiterStringWithEscape() throws IOException {
        final String source = "a![!|!]b![|]c[|]xyz\r\nabc[abc][|]xyz";
        final CSVFormat csvFormat = new CSVFormatBuilder().setDelimiter("[|]").setEscape('!').build();
        try (ICSVParser ICSVParser = new CSVParser(new StringReader(source), csvFormat)) {
            CSVRecord csvRecord = ICSVParser.nextRecord();
            assertEquals("a[|]b![|]c", csvRecord.get(0));
            assertEquals("xyz", csvRecord.get(1));
            csvRecord = ICSVParser.nextRecord();
            assertEquals("abc[abc]", csvRecord.get(0));
            assertEquals("xyz", csvRecord.get(1));
        }
    }

    @Test
    public void testParseWithDelimiterStringWithQuote() throws IOException {
        final String source = "'a[|]b[|]c'[|]xyz\r\nabc[abc][|]xyz";
        final CSVFormat csvFormat = new CSVFormatBuilder().setDelimiter("[|]").setQuote('\'').build();
        try (ICSVParser ICSVParser = new CSVParser(new StringReader(source), csvFormat)) {
            CSVRecord csvRecord = ICSVParser.nextRecord();
            assertEquals("a[|]b[|]c", csvRecord.get(0));
            assertEquals("xyz", csvRecord.get(1));
            csvRecord = ICSVParser.nextRecord();
            assertEquals("abc[abc]", csvRecord.get(0));
            assertEquals("xyz", csvRecord.get(1));
        }
    }

    @Test
    public void testParseWithDelimiterWithEscape() throws IOException {
        final String source = "a!,b!,c,xyz";
        final CSVFormat csvFormat = new CSVFormatBuilder().setEscape('!').build();
        try (ICSVParser ICSVParser =  new CSVParser(new StringReader(source), csvFormat)) {
            final CSVRecord csvRecord = ICSVParser.nextRecord();
            assertEquals("a,b,c", csvRecord.get(0));
            assertEquals("xyz", csvRecord.get(1));
        }
    }

    @Test
    public void testParseWithDelimiterWithQuote() throws IOException {
        final String source = "'a,b,c',xyz";
        final CSVFormat csvFormat = new CSVFormatBuilder().setQuote('\'').build();
        try (ICSVParser ICSVParser = new CSVParser(new StringReader(source), csvFormat)) {
            final CSVRecord csvRecord = ICSVParser.nextRecord();
            assertEquals("a,b,c", csvRecord.get(0));
            assertEquals("xyz", csvRecord.get(1));
        }
    }

    @Test
    public void testParseWithQuoteThrowsException() {
        final CSVFormat csvFormat = new CSVFormatBuilder().setQuote('\'').build();
        assertThrows(IOException.class, () -> new CSVParser(new StringReader("'a,b,c','"), csvFormat).nextRecord());
        assertThrows(IOException.class, () -> new CSVParser(new StringReader("'a,b,c'abc,xyz"), csvFormat).nextRecord());
        assertThrows(IOException.class, () -> new CSVParser(new StringReader("'abc'a,b,c',xyz"), csvFormat).nextRecord());
    }

    @Test
    public void testParseWithQuoteWithEscape() throws IOException {
        final String source = "'a?,b?,c?d',xyz";
        final CSVFormat csvFormat = new CSVFormatBuilder().setQuote('\'').setEscape('?').build();
        try (ICSVParser ICSVParser = new CSVParser(new StringReader(source), csvFormat)) {
            final CSVRecord csvRecord = ICSVParser.nextRecord();
            assertEquals("a,b,c?d", csvRecord.get(0));
            assertEquals("xyz", csvRecord.get(1));
        }
    }


}
