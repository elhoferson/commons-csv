/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.csv.printer;

import org.apache.commons.csv.format.CSVFormatBuilder;
import org.apache.commons.csv.format.ICSVFormat;
import org.apache.commons.csv.format.QuoteMode;
import org.apache.commons.csv.parser.ExtendedBufferedReader;
import org.apache.commons.csv.util.IOUtils;

import java.io.*;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import static org.apache.commons.csv.Constants.*;

/**
 * Prints values in a {@link ICSVFormat CSV format}.
 *
 * <p>Values can be appended to the output by calling the {@link #print(Object)} method.
 * Values are printed according to {@link String#valueOf(Object)}.
 * To complete a record the {@link #println()} method has to be called.
 * Comments can be appended by calling {@link #printComment(String)}.
 * However a comment will only be written to the output if the {@link ICSVFormat} supports comments.
 * </p>
 *
 * <p>The printer also supports appending a complete record at once by calling {@link #printRecord(Object...)}
 * or {@link #printRecord(Iterable)}.
 * Furthermore {@link #printRecords(Object...)}, {@link #printRecords(Iterable)} and {@link #printRecords(ResultSet)}
 * methods can be used to print several records at once.
 * </p>
 *
 * <p>Example:</p>
 *
 * <pre>
 * try (CSVPrinter printer = new CSVPrinter(new FileWriter("csv.txt"), CSVFormat.EXCEL)) {
 *     printer.printRecord("id", "userName", "firstName", "lastName", "birthday");
 *     printer.printRecord(1, "john73", "John", "Doe", LocalDate.of(1973, 9, 15));
 *     printer.println();
 *     printer.printRecord(2, "mary", "Mary", "Meyer", LocalDate.of(1985, 3, 29));
 * } catch (IOException ex) {
 *     ex.printStackTrace();
 * }
 * </pre>
 *
 * <p>This code will write the following to csv.txt:</p>
 * <pre>
 * id,userName,firstName,lastName,birthday
 * 1,john73,John,Doe,1973-09-15
 *
 * 2,mary,Mary,Meyer,1985-03-29
 * </pre>
 */
public class CSVPrinter implements ICSVPrinter {

    /** The place that the values get written. */
    private final Appendable appendable;
    private final ICSVFormat format;

    /** True if we just began a new record. */
    private boolean newRecord = true;

    /**
     * Creates a printer that will print values to the given stream following the CSVFormat.
     * <p>
     * Currently, only a pure encapsulation format or a pure escaping format is supported. Hybrid formats (encapsulation
     * and escaping with a different character) are not supported.
     * </p>
     *
     * @param appendable
     *            stream to which to print. Must not be null.
     * @param format
     *            the CSV format. Must not be null.
     * @throws IOException
     *             thrown if the optional header cannot be printed.
     * @throws IllegalArgumentException
     *             thrown if the parameters of the format are inconsistent or if either out or format are null.
     */
    public CSVPrinter(final Appendable appendable, final ICSVFormat format) throws IOException {
        Objects.requireNonNull(appendable, "appendable");
        Objects.requireNonNull(format, "format");

        this.appendable = appendable;
        this.format = format.copy();
        // TODO: Is it a good idea to do this here instead of on the first call to a print method?
        // It seems a pain to have to track whether the header has already been printed or not.
        if (format.getHeaderComments() != null) {
            for (final String line : format.getHeaderComments()) {
                this.printComment(line);
            }
        }
        if (format.getHeader() != null && !format.getSkipHeaderRecord()) {
            this.printRecord((Object[]) format.getHeader());
        }
    }

    @Override
    public void close() throws IOException {
        close(false);
    }

    /**
     * Closes the underlying stream with an optional flush first.
     * @param flush whether to flush before the actual close.
     *
     * @throws IOException
     *             If an I/O error occurs
     * @since 1.6
     */
    public void close(final boolean flush) throws IOException {
        if (flush || format.getAutoFlush()) {
            flush();
        }
        if (appendable instanceof Closeable) {
            ((Closeable) appendable).close();
        }
    }

    /**
     * Flushes the underlying stream.
     *
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void flush() throws IOException {
        if (appendable instanceof Flushable) {
            ((Flushable) appendable).flush();
        }
    }

    /**
     * Gets the target Appendable.
     *
     * @return the target Appendable.
     */
    public Appendable getOut() {
        return this.appendable;
    }

    /**
     * Prints the string as the next value on the line. The value will be escaped or encapsulated as needed.
     *
     * @param value
     *            value to be output.
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void print(final Object value) throws IOException {
        print(value, newRecord);
        newRecord = false;
    }

    /**
     * Prints a comment on a new line among the delimiter separated values.
     *
     * <p>
     * Comments will always begin on a new line and occupy at least one full line. The character specified to start
     * comments and a space will be inserted at the beginning of each new line in the comment.
     * </p>
     *
     * <p>
     * If comments are disabled in the current CSV format this method does nothing.
     * </p>
     *
     * <p>This method detects line breaks inside the comment string and inserts {@link ICSVFormat#getRecordSeparator()}
     * to start a new line of the comment. Note that this might produce unexpected results for formats that do not use
     * line breaks as record separator.</p>
     *
     * @param comment
     *            the comment to output
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void printComment(final String comment) throws IOException {
        if (comment == null || !format.isCommentMarkerSet()) {
            return;
        }
        if (!newRecord) {
            println();
        }
        appendable.append(format.getCommentMarker());
        appendable.append(SP);
        for (int i = 0; i < comment.length(); i++) {
            final char c = comment.charAt(i);
            switch (c) {
            case CR:
                if (i + 1 < comment.length() && comment.charAt(i + 1) == LF) {
                    i++;
                }
                //$FALL-THROUGH$ break intentionally excluded.
            case LF:
                println();
                appendable.append(format.getCommentMarker());
                appendable.append(SP);
                break;
            default:
                appendable.append(c);
                break;
            }
        }
        println();
    }

    /**
     * Prints headers for a result set based on its metadata.
     *
     * @param resultSet The result set to query for metadata.
     * @throws IOException If an I/O error occurs.
     * @throws SQLException If a database access error occurs or this method is called on a closed result set.
     * @since 1.9.0
     */
    @Override
    public void printHeaders(final ResultSet resultSet) throws IOException, SQLException {
        printRecord((Object[]) new CSVFormatBuilder().setHeader(resultSet).build().getHeader());
    }

    /**
     * Prints the given values a single record of delimiter separated values followed by the record separator.
     *
     * <p>
     * The values will be quoted if needed. Quotes and newLine characters will be escaped. This method adds the record
     * separator to the output after printing the record, so there is no need to call {@link #println()}.
     * </p>
     *
     * @param values
     *            values to output.
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void printRecord(final Iterable<?> values) throws IOException {
        for (final Object value : values) {
            print(value);
        }
        println();
    }

    /**
     * Prints the given values a single record of delimiter separated values followed by the record separator.
     *
     * <p>
     * The values will be quoted if needed. Quotes and newLine characters will be escaped. This method adds the record
     * separator to the output after printing the record, so there is no need to call {@link #println()}.
     * </p>
     *
     * @param values
     *            values to output.
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void printRecord(final Object... values) throws IOException {
        printRecord(Arrays.asList(values));
    }

    /**
     * Prints all the objects in the given collection handling nested collections/arrays as records.
     *
     * <p>
     * If the given collection only contains simple objects, this method will print a single record like
     * {@link #printRecord(Iterable)}. If the given collections contains nested collections/arrays those nested elements
     * will each be printed as records using {@link #printRecord(Object...)}.
     * </p>
     *
     * <p>
     * Given the following data structure:
     * </p>
     *
     * <pre>
     * <code>
     * List&lt;String[]&gt; data = ...
     * data.add(new String[]{ "A", "B", "C" });
     * data.add(new String[]{ "1", "2", "3" });
     * data.add(new String[]{ "A1", "B2", "C3" });
     * </code>
     * </pre>
     *
     * <p>
     * Calling this method will print:
     * </p>
     *
     * <pre>
     * <code>
     * A, B, C
     * 1, 2, 3
     * A1, B2, C3
     * </code>
     * </pre>
     *
     * @param values
     *            the values to print.
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void printRecords(final Iterable<?> values) throws IOException {
        for (final Object value : values) {
            if (value instanceof Object[]) {
                this.printRecord((Object[]) value);
            } else if (value instanceof Iterable) {
                this.printRecord((Iterable<?>) value);
            } else {
                this.printRecord(value);
            }
        }
    }

    /**
     * Prints all the objects in the given array handling nested collections/arrays as records.
     *
     * <p>
     * If the given array only contains simple objects, this method will print a single record like
     * {@link #printRecord(Object...)}. If the given collections contains nested collections/arrays those nested
     * elements will each be printed as records using {@link #printRecord(Object...)}.
     * </p>
     *
     * <p>
     * Given the following data structure:
     * </p>
     *
     * <pre>
     * <code>
     * String[][] data = new String[3][]
     * data[0] = String[]{ "A", "B", "C" };
     * data[1] = new String[]{ "1", "2", "3" };
     * data[2] = new String[]{ "A1", "B2", "C3" };
     * </code>
     * </pre>
     *
     * <p>
     * Calling this method will print:
     * </p>
     *
     * <pre>
     * <code>
     * A, B, C
     * 1, 2, 3
     * A1, B2, C3
     * </code>
     * </pre>
     *
     * @param values
     *            the values to print.
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void printRecords(final Object... values) throws IOException {
        printRecords(Arrays.asList(values));
    }

    /**
     * Prints all the objects in the given JDBC result set.
     *
     * @param resultSet
     *            result set the values to print.
     * @throws IOException
     *             If an I/O error occurs
     * @throws SQLException
     *             if a database access error occurs
     */
    @Override
    public void printRecords(final ResultSet resultSet) throws SQLException, IOException {
        final int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                final Object object = resultSet.getObject(i);
                // TODO Who manages the Clob? The JDBC driver or must we close it? Is it driver-dependent?
                print(object instanceof Clob ? ((Clob) object).getCharacterStream() : object);
            }
            println();
        }
    }

    /**
     * Prints all the objects with metadata in the given JDBC result set based on the header boolean.
     *
     * @param resultSet source of row data.
     * @param printHeader whether to print headers.
     * @throws IOException If an I/O error occurs
     * @throws SQLException if a database access error occurs
     * @since 1.9.0
     */
    @Override
    public void printRecords(final ResultSet resultSet, final boolean printHeader) throws SQLException, IOException {
        if (printHeader) {
            printHeaders(resultSet);
        }
        printRecords(resultSet);
    }

    private void print(final Object object, final CharSequence value, final boolean newRecord) throws IOException {
        final int offset = 0;
        final int len = value.length();
        if (!newRecord) {
            append(format.getDelimiterString());
        }
        if (object == null) {
            append(value);
        } else if (format.isQuoteCharacterSet()) {
            // the original object is needed so can check for Number
            printWithQuotes(object, value, newRecord);
        } else if (format.isEscapeCharacterSet()) {
            printWithEscapes(value);
        } else {
            appendable.append(value, offset, len);
        }
    }

    /**
     * Prints the {@code value} as the next value on the line to {@code out}. The value will be escaped or encapsulated as needed. Useful when one wants to
     * avoid creating CSVPrinters. Trims the value if {@link ICSVFormat#getTrim()} is true.
     *
     * @param value     value to output.
     * @param newRecord if this a new record.
     * @throws IOException If an I/O error occurs.
     * @since 1.4
     */
    @Override
    public void print(final Object value, final boolean newRecord) throws IOException {
        // null values are considered empty
        // Only call CharSequence.toString() if you have to, helps GC-free use cases.
        CharSequence charSequence;
        if (value == null) {
            // https://issues.apache.org/jira/browse/CSV-203
            if (null == format.getNullString()) {
                charSequence = EMPTY;
            } else if (QuoteMode.ALL == format.getQuoteMode()) {
                charSequence = format.getQuotedNullString();
            } else {
                charSequence = format.getNullString();
            }
        } else if (value instanceof CharSequence) {
            charSequence = (CharSequence) value;
        } else if (value instanceof Reader) {
            print((Reader) value, newRecord);
            return;
        } else {
            charSequence = value.toString();
        }
        charSequence = format.getTrim() ? trim(charSequence) : charSequence;
        print(value, charSequence, newRecord);
    }

    private void print(final Reader reader, final boolean newRecord) throws IOException {
        // Reader is never null
        if (!newRecord) {
            append(format.getDelimiterString());
        }
        if (format.isQuoteCharacterSet()) {
            printWithQuotes(reader);
        } else if (format.isEscapeCharacterSet()) {
            printWithEscapes(reader);
        } else if (appendable instanceof Writer) {
            IOUtils.copyLarge(reader, (Writer) appendable);
        } else {
            IOUtils.copy(reader, appendable);
        }

    }

    /*
     * Note: Must only be called if escaping is enabled, otherwise will generate NPE.
     */
    private void printWithEscapes(final CharSequence charSeq) throws IOException {
        int start = 0;
        int pos = 0;
        final int end = charSeq.length();

        final char[] delim = format.getDelimiterString().toCharArray();
        final int delimLength = delim.length;
        final char escape = format.getEscapeCharacter();

        while (pos < end) {
            char c = charSeq.charAt(pos);
            final boolean isDelimiterStart = isDelimiter(c, charSeq, pos, delim, delimLength);
            if (c == CR || c == LF || c == escape || isDelimiterStart) {
                // write out segment up until this char
                if (pos > start) {
                    appendable.append(charSeq, start, pos);
                }
                if (c == LF) {
                    c = 'n';
                } else if (c == CR) {
                    c = 'r';
                }

                appendable.append(escape);
                appendable.append(c);

                if (isDelimiterStart) {
                    for (int i = 1; i < delimLength; i++) {
                        pos++;
                        c = charSeq.charAt(pos);
                        appendable.append(escape);
                        appendable.append(c);
                    }
                }

                start = pos + 1; // start on the current char after this one
            }
            pos++;
        }

        // write last segment
        if (pos > start) {
            appendable.append(charSeq, start, pos);
        }
    }

    private void printWithEscapes(final Reader reader) throws IOException {
        int start = 0;
        int pos = 0;

        @SuppressWarnings("resource") // Temp reader on input reader.
        final ExtendedBufferedReader bufferedReader = new ExtendedBufferedReader(reader);
        final char[] delim = format.getDelimiterString().toCharArray();
        final int delimLength = delim.length;
        final char escape = format.getEscapeCharacter();
        final StringBuilder builder = new StringBuilder(IOUtils.DEFAULT_BUFFER_SIZE);

        int c;
        while (-1 != (c = bufferedReader.read())) {
            builder.append((char) c);
            final boolean isDelimiterStart = isDelimiter((char) c, builder.toString() + new String(bufferedReader.lookAhead(delimLength - 1)), pos, delim,
                    delimLength);
            if (c == CR || c == LF || c == escape || isDelimiterStart) {
                // write out segment up until this char
                if (pos > start) {
                    append(builder.substring(start, pos));
                    builder.setLength(0);
                    pos = -1;
                }
                if (c == LF) {
                    c = 'n';
                } else if (c == CR) {
                    c = 'r';
                }

                append(escape);
                append((char) c);

                if (isDelimiterStart) {
                    for (int i = 1; i < delimLength; i++) {
                        c = bufferedReader.read();
                        append(escape);
                        append((char) c);
                    }
                }

                start = pos + 1; // start on the current char after this one
            }
            pos++;
        }

        // write last segment
        if (pos > start) {
            append(builder.substring(start, pos));
        }
    }

    /*
     * Note: must only be called if quoting is enabled, otherwise will generate NPE
     */
    // the original object is needed so can check for Number
    private void printWithQuotes(final Object object, final CharSequence charSeq, final boolean newRecord) throws IOException {
        boolean quote = false;
        int start = 0;
        int pos = 0;
        final int len = charSeq.length();

        final char[] delim = format.getDelimiterString().toCharArray();
        final int delimLength = delim.length;
        final char quoteChar = format.getQuoteCharacter();
        // If escape char not specified, default to the quote char
        // This avoids having to keep checking whether there is an escape character
        // at the cost of checking against quote twice
        final char escapeChar = format.isEscapeCharacterSet() ? format.getEscapeCharacter() : quoteChar;

        QuoteMode quoteModePolicy = format.getQuoteMode();
        if (quoteModePolicy == null) {
            quoteModePolicy = QuoteMode.MINIMAL;
        }
        switch (quoteModePolicy) {
            case ALL:
            case ALL_NON_NULL:
                quote = true;
                break;
            case NON_NUMERIC:
                quote = !(object instanceof Number);
                break;
            case NONE:
                // Use the existing escaping code
                printWithEscapes(charSeq);
                return;
            case MINIMAL:
                if (len <= 0) {
                    // always quote an empty token that is the first
                    // on the line, as it may be the only thing on the
                    // line. If it were not quoted in that case,
                    // an empty line has no tokens.
                    if (newRecord) {
                        quote = true;
                    }
                } else {
                    char c = charSeq.charAt(pos);

                    if (c <= COMMENT) {
                        // Some other chars at the start of a value caused the parser to fail, so for now
                        // encapsulate if we start in anything less than '#'. We are being conservative
                        // by including the default comment char too.
                        quote = true;
                    } else {
                        while (pos < len) {
                            c = charSeq.charAt(pos);
                            if (c == LF || c == CR || c == quoteChar || c == escapeChar || isDelimiter(c, charSeq, pos, delim, delimLength)) {
                                quote = true;
                                break;
                            }
                            pos++;
                        }

                        if (!quote) {
                            pos = len - 1;
                            c = charSeq.charAt(pos);
                            // Some other chars at the end caused the parser to fail, so for now
                            // encapsulate if we end in anything less than ' '
                            if (c <= SP) {
                                quote = true;
                            }
                        }
                    }
                }

                if (!quote) {
                    // no encapsulation needed - write out the original value
                    appendable.append(charSeq, start, len);
                    return;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Quote value: " + quoteModePolicy);
        }

        if (!quote) {
            // no encapsulation needed - write out the original value
            appendable.append(charSeq, start, len);
            return;
        }

        // we hit something that needed encapsulation
        appendable.append(quoteChar);

        // Pick up where we left off: pos should be positioned on the first character that caused
        // the need for encapsulation.
        while (pos < len) {
            final char c = charSeq.charAt(pos);
            if (c == quoteChar || c == escapeChar) {
                // write out the chunk up until this point
                appendable.append(charSeq, start, pos);
                append(escapeChar); // now output the escape
                start = pos; // and restart with the matched char
            }
            pos++;
        }

        // write the last segment
        appendable.append(charSeq, start, pos);
        append(quoteChar);
    }

    /**
     * Always use quotes unless QuoteMode is NONE, so we not have to look ahead.
     *
     * @param reader What to print
     * @throws IOException If an I/O error occurs
     */
    private void printWithQuotes(final Reader reader) throws IOException {

        if (format.getQuoteMode() == QuoteMode.NONE) {
            printWithEscapes(reader);
            return;
        }

        int pos = 0;

        final char quote = format.getQuoteCharacter();
        final StringBuilder builder = new StringBuilder(IOUtils.DEFAULT_BUFFER_SIZE);

        append(quote);

        int c;
        while (-1 != (c = reader.read())) {
            builder.append((char) c);
            if (c == quote) {
                // write out segment up until this char
                if (pos > 0) {
                    append(builder.substring(0, pos));
                    append(quote);
                    builder.setLength(0);
                    pos = -1;
                }

                append((char) c);
            }
            pos++;
        }

        // write last segment
        if (pos > 0) {
            append(builder.substring(0, pos));
        }

        append(quote);
    }

    /**
     * Outputs the trailing delimiter (if set) followed by the record separator (if set).
     *
     * @throws IOException If an I/O error occurs.
     * @since 1.4
     */
    @Override
    public void println() throws IOException {
        if (format.getTrailingDelimiter()) {
            append(format.getDelimiterString());
        }
        if (format.getRecordSeparator() != null) {
            append(format.getRecordSeparator());
        }

        newRecord = true;
    }

    /**
     * Matches whether the next characters constitute a delimiter
     *
     * @param ch
     *            the current char
     * @param charSeq
     *            the match char sequence
     * @param startIndex
     *            where start to match
     * @param delimiter
     *            the delimiter
     * @param delimiterLength
     *            the delimiter length
     * @return true if the match is successful
     */
    private boolean isDelimiter(final char ch, final CharSequence charSeq, final int startIndex, final char[] delimiter, final int delimiterLength) {
        if (ch != delimiter[0]) {
            return false;
        }
        final int len = charSeq.length();
        if (startIndex + delimiterLength > len) {
            return false;
        }
        for (int i = 1; i < delimiterLength; i++) {
            if (charSeq.charAt(startIndex + i) != delimiter[i]) {
                return false;
            }
        }
        return true;
    }

    private void append(final CharSequence csq) throws IOException {
        //try {
        appendable.append(csq);
        //} catch (final IOException e) {
        //    throw new UncheckedIOException(e);
        //}
    }

    private void append(final char c) throws IOException {
        //try {
        appendable.append(c);
        //} catch (final IOException e) {
        //    throw new UncheckedIOException(e);
        //}
    }

    static CharSequence trim(final CharSequence charSequence) {
        if (charSequence instanceof String) {
            return ((String) charSequence).trim();
        }
        final int count = charSequence.length();
        int len = count;
        int pos = 0;

        while (pos < len && charSequence.charAt(pos) <= SP) {
            pos++;
        }
        while (pos < len && charSequence.charAt(len - 1) <= SP) {
            len--;
        }
        return pos > 0 || len < count ? charSequence.subSequence(pos, len) : charSequence;
    }
}
