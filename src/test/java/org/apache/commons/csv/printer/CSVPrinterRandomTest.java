package org.apache.commons.csv.printer;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.parser.CSVParser;
import org.apache.commons.csv.parser.ICSVParser;
import org.apache.commons.csv.record.CSVRecord;
import org.apache.commons.csv.util.Utils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.List;
import java.util.Random;

import static org.apache.commons.csv.Constants.BACKSLASH;

public class CSVPrinterRandomTest extends AbstractCSVPrinterTest {

    private static final int ITERATIONS_FOR_RANDOM_TEST = 50000;

    private static String printable(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (ch <= ' ' || ch >= 128) {
                sb.append("(").append((int) ch).append(")");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private void doOneRandom(final CSVFormat format) throws Exception {
        final Random r = new Random();

        final int nLines = r.nextInt(4) + 1;
        final int nCol = r.nextInt(3) + 1;
        // nLines=1;nCol=2;
        final String[][] lines = generateLines(nLines, nCol);

        final StringWriter sw = new StringWriter();
        try (final CSVPrinter printer = new CSVPrinter(sw, format)) {

            for (int i = 0; i < nLines; i++) {
                // for (int j=0; j<lines[i].length; j++) System.out.println("### VALUE=:" + printable(lines[i][j]));
                printer.printRecord((Object[]) lines[i]);
            }

            printer.flush();
        }
        final String result = sw.toString();
        // System.out.println("### :" + printable(result));

        try (final ICSVParser parser = CSVParser.parse(result, format)) {
            final List<CSVRecord> parseResult = parser.getRecords();

            final String[][] expected = lines.clone();
            for (int i = 0; i < expected.length; i++) {
                expected[i] = expectNulls(expected[i], format);
            }
            Utils.compare("Printer output :" + printable(result), expected, parseResult);
        }
    }

    private void doRandom(final CSVFormat format, final int iter) throws Exception {
        for (int i = 0; i < iter; i++) {
            doOneRandom(format);
        }
    }


    private String[][] generateLines(final int nLines, final int nCol) {
        final String[][] lines = new String[nLines][];
        for (int i = 0; i < nLines; i++) {
            final String[] line = new String[nCol];
            lines[i] = line;
            for (int j = 0; j < nCol; j++) {
                line[j] = randStr();
            }
        }
        return lines;
    }


    private String randStr() {
        final Random r = new Random();

        final int sz = r.nextInt(20);
        // sz = r.nextInt(3);
        final char[] buf = new char[sz];
        for (int i = 0; i < sz; i++) {
            // stick in special chars with greater frequency
            final char ch;
            final int what = r.nextInt(20);
            switch (what) {
                case 0:
                    ch = '\r';
                    break;
                case 1:
                    ch = '\n';
                    break;
                case 2:
                    ch = '\t';
                    break;
                case 3:
                    ch = '\f';
                    break;
                case 4:
                    ch = ' ';
                    break;
                case 5:
                    ch = ',';
                    break;
                case 6:
                    ch = DQUOTE_CHAR;
                    break;
                case 7:
                    ch = '\'';
                    break;
                case 8:
                    ch = BACKSLASH;
                    break;
                default:
                    ch = (char) r.nextInt(300);
                    break;
                // default: ch = 'a'; break;
            }
            buf[i] = ch;
        }
        return new String(buf);
    }


    @Test
    public void testRandomDefault() throws Exception {
        doRandom(CSVFormatPredefinedFormats.Default.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }

    @Test
    public void testRandomExcel() throws Exception {
        doRandom(CSVFormatPredefinedFormats.Excel.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }

    @Test
    @Disabled
    public void testRandomMongoDbCsv() throws Exception {
        doRandom(CSVFormatPredefinedFormats.MongoDBCsv.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }

    @Test
    public void testRandomMySql() throws Exception {
        doRandom(CSVFormatPredefinedFormats.MySQL.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }

    @Test
    @Disabled
    public void testRandomOracle() throws Exception {
        doRandom(CSVFormatPredefinedFormats.Oracle.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }

    @Test
    @Disabled
    public void testRandomPostgreSqlCsv() throws Exception {
        doRandom(CSVFormatPredefinedFormats.PostgreSQLCsv.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }

    @Test
    @Disabled
    public void testRandomPostgreSqlText() throws Exception {
        doRandom(CSVFormatPredefinedFormats.PostgreSQLText.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }


    @Test
    public void testRandomRfc4180() throws Exception {
        doRandom(CSVFormatPredefinedFormats.RFC4180.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }

    @Test
    public void testRandomTdf() throws Exception {
        doRandom(CSVFormatPredefinedFormats.TDF.getFormat(), ITERATIONS_FOR_RANDOM_TEST);
    }

}
