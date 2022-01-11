package org.apache.commons.csv.printer;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.util.IOUtils;
import org.apache.commons.csv.format.QuoteMode;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CSVPrinterSqlTest extends AbstractCSVPrinterTest {


    private String longText2;

    @Test
    public void testJdbcPrinter() throws IOException, ClassNotFoundException, SQLException {
        final StringWriter sw = new StringWriter();
        try (final Connection connection = getH2Connection()) {
            setUpTable(connection);
            try (final Statement stmt = connection.createStatement();
                 final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat());
                 final ResultSet resultSet = stmt.executeQuery("select ID, NAME, TEXT from TEST");) {
                printer.printRecords(resultSet);
            }
        }
        assertEquals("1,r1,\"long text 1\"" + recordSeparator + "2,r2,\"" + longText2 + "\"" + recordSeparator, sw.toString());
    }

    @Test
    public void testJdbcPrinterWithResultSet() throws IOException, ClassNotFoundException, SQLException {
        final StringWriter sw = new StringWriter();
        Class.forName("org.h2.Driver");
        try (final Connection connection = getH2Connection()) {
            setUpTable(connection);
            CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
            try (final Statement stmt = connection.createStatement();
                 final ResultSet resultSet = stmt.executeQuery("select ID, NAME, TEXT from TEST")) {
                format.setHeader(resultSet);
                try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
                    printer.printRecords(resultSet);
                }
            }
        }
        assertEquals("ID,NAME,TEXT" + recordSeparator + "1,r1,\"long text 1\"" + recordSeparator + "2,r2,\"" + longText2
                + "\"" + recordSeparator, sw.toString());
    }

    @Test
    public void testJdbcPrinterWithResultSetHeader() throws IOException, ClassNotFoundException, SQLException {
        final StringWriter sw = new StringWriter();
        try (final Connection connection = getH2Connection()) {
            setUpTable(connection);
            try (final Statement stmt = connection.createStatement();
                 final CSVPrinter printer = new CSVPrinter(sw, CSVFormatPredefinedFormats.Default.getFormat());) {
                try (final ResultSet resultSet = stmt.executeQuery("select ID, NAME from TEST")) {
                    printer.printRecords(resultSet, true);
                    assertEquals("ID,NAME" + recordSeparator + "1,r1" + recordSeparator + "2,r2" + recordSeparator,
                            sw.toString());
                }
                try (final ResultSet resultSet = stmt.executeQuery("select ID, NAME from TEST")) {
                    printer.printRecords(resultSet, false);
                    assertNotEquals("ID,NAME" + recordSeparator + "1,r1" + recordSeparator + "2,r2" + recordSeparator,
                            sw.toString());
                }
            }
        }
    }

    @Test
    public void testJdbcPrinterWithResultSetMetaData() throws IOException, ClassNotFoundException, SQLException {
        final StringWriter sw = new StringWriter();
        Class.forName("org.h2.Driver");
        try (final Connection connection = getH2Connection()) {
            setUpTable(connection);
            CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();
            try (final Statement stmt = connection.createStatement();
                 final ResultSet resultSet = stmt.executeQuery("select ID, NAME, TEXT from TEST")) {
                format.setHeader(resultSet.getMetaData());
                try (final CSVPrinter printer = new CSVPrinter(sw, format)) {
                    printer.printRecords(resultSet);
                }

                assertEquals("ID,NAME,TEXT" + recordSeparator + "1,r1,\"long text 1\"" + recordSeparator + "2,r2,\""
                        + longText2 + "\"" + recordSeparator, sw.toString());
            }
        }
    }

    @Test
    public void testMySqlNullOutput() throws IOException {
        Object[] s = new String[] { "NULL", null };
        String expected = "\"NULL\"\tNULL\n";
        CSVFormat format = CSVFormatPredefinedFormats.MySQL.getFormat();
        format.setQuoteCharacter(DQUOTE_CHAR);
        format.setNullString("NULL");
        format.setQuoteMode(QuoteMode.NON_NUMERIC);
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, false);

        s = new String[] { "\\N", null };
        format = CSVFormatPredefinedFormats.MySQL.getFormat();
        format.setNullString("\\N");
        expected = "\\\\N\t\\N\n";
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, true);

        s = new String[] { "\\N", "A" };
        expected = "\\\\N\tA\n";
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, true);

        s = new String[] { "\n", "A" };
        expected = "\\n\tA\n";
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, true);

        s = new String[] { "", null };
        format = CSVFormatPredefinedFormats.MySQL.getFormat();
        format.setNullString("NULL");
        expected = "\tNULL\n";
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, true);

        s = new String[] { "", null };
        format = CSVFormatPredefinedFormats.MySQL.getFormat();
        expected = "\t\\N\n";
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, true);

        s = new String[] { "\\N", "", "\u000e,\\\r" };
        expected = "\\\\N\t\t\u000e,\\\\\\r\n";
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, true);

        s = new String[] { "NULL", "\\\r" };
        expected = "NULL\t\\\\\\r\n";
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, true);

        s = new String[] { "\\\r" };
        expected = "\\\\\\r\n";
        compareActualOutputWithExpectedOutputMySQL(s, expected, format, true);
    }

    @Test
    public void testMySqlNullStringDefault() {
        assertEquals("\\N", CSVFormatPredefinedFormats.MySQL.getFormat().getNullString());
    }


    @Test
    public void testPostgreSqlCsvNullOutput() throws IOException {
        Object[] s = new String[] { "NULL", null };
        CSVFormat format = CSVFormatPredefinedFormats.PostgreSQLCsv.getFormat();
        format.setQuoteCharacter(DQUOTE_CHAR);
        format.setNullString("NULL");
        format.setQuoteMode(QuoteMode.ALL_NON_NULL);
        String expected = "\"NULL\",NULL\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "\\N", null };
        format = CSVFormatPredefinedFormats.PostgreSQLCsv.getFormat();
        format.setNullString("\\N");
        expected = "\"\\N\",\\N\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "\\N", "A" };
        expected = "\"\\N\",\"A\"\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "\n", "A" };
        expected = "\"\n\",\"A\"\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "", null };
        expected = "\"\",\\N\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "", null };
        format = CSVFormatPredefinedFormats.PostgreSQLCsv.getFormat();
        expected = "\"\",\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "NULL", "\\\r" };
        expected = "\"NULL\",\"\\\r\"\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "\\\r" };
        expected = "\"\\\r\"\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);
    }

    @Test
    public void testPostgreSqlCsvTextOutput() throws IOException {
        Object[] s = new String[] { "NULL", null };
        CSVFormat format = CSVFormatPredefinedFormats.PostgreSQLText.getFormat();
        format.setQuoteCharacter(DQUOTE_CHAR);
        format.setNullString("NULL");
        format.setQuoteMode(QuoteMode.ALL_NON_NULL);
        String expected = "\"NULL\"\tNULL\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "\\N", null };
        format = CSVFormatPredefinedFormats.PostgreSQLText.getFormat();
        format.setNullString("\\N");
        expected = "\"\\\\N\"\t\\N\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "\\N", "A" };
        expected = "\"\\\\N\"\t\"A\"\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "\n", "A" };
        expected = "\"\n\"\t\"A\"\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "", null };
        format = CSVFormatPredefinedFormats.PostgreSQLText.getFormat();
        format.setNullString("NULL");
        expected = "\"\"\tNULL\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "", null };
        format = CSVFormatPredefinedFormats.PostgreSQLText.getFormat();
        expected = "\"\"\t\\N\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "NULL", "\\\r" };
        expected = "\"NULL\"\t\"\\\\\r\"\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);

        s = new String[] { "\\\r" };
        expected = "\"\\\\\r\"\n";
        compareActualOutputWithExpectedOutputPostgres(s, expected, format);
    }

    @Test
    public void testPostgreSqlNullStringDefaultCsv() {
        assertEquals("", CSVFormatPredefinedFormats.PostgreSQLCsv.getFormat().getNullString());
    }

    @Test
    public void testPostgreSqlNullStringDefaultText() {
        assertEquals("\\N", CSVFormatPredefinedFormats.PostgreSQLText.getFormat().getNullString());
    }

    private void setUpTable(final Connection connection) throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255), TEXT CLOB)");
            statement.execute("insert into TEST values(1, 'r1', 'long text 1')");
            longText2 = StringUtils.repeat('a', IOUtils.DEFAULT_BUFFER_SIZE - 4);
            longText2 += "\"\r\n\"a\"";
            longText2 += StringUtils.repeat('a', IOUtils.DEFAULT_BUFFER_SIZE - 1);
            statement.execute("insert into TEST values(2, 'r2', '" + longText2 + "')");
            longText2 = longText2.replace("\"","\"\"");
        }
    }

    /***
     * Compares the actual output from the CSVPrinter with the expected output for MySQL
     * @param s the object that serves as input for record
     * @param expected the expected string
     * @param format the CSVFormat
     * @param expectNulls true, if nulls are expected within the output
     * @throws IOException is thrown in case of exceptions withing the CSVPrinter
     */
    private void compareActualOutputWithExpectedOutputMySQL(Object[] s,
                                                            String expected,
                                                            CSVFormat format,
                                                            boolean expectNulls) throws IOException {
        StringWriter writer = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(writer, format)) {
            printer.printRecord(s);
        }

        assertEquals(expected, writer.toString());
        String[] record0 = toFirstRecordValues(expected, format);

        if (expectNulls) {
            assertArrayEquals(expectNulls(s, format), record0);
        }
        else {
            assertArrayEquals(s, record0);
        }
    }

    /***
     * Compares the actual output from the CSVPrinter with the expected output for Postgres
     * @param s the object that serves as input for record
     * @param expected the expected string
     * @param format the CSVFormat
     * @throws IOException is thrown in case of exceptions withing the CSVPrinter
     */
    private void compareActualOutputWithExpectedOutputPostgres(Object[] s,
                                                               String expected,
                                                               CSVFormat format) throws IOException {
        StringWriter writer = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(writer, format)) {
            printer.printRecord(s);
        }

        assertEquals(expected, writer.toString());
    }

    private Connection getH2Connection() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:mem:my_test;", "sa", "");
    }
}
