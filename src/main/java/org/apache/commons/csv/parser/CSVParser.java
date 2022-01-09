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

package org.apache.commons.csv.parser;

import org.apache.commons.csv.format.ICSVFormat;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Parses CSV files according to the specified format.
 *
 * Because CSV appears in many different dialects, the parser supports many formats by allowing the
 * specification of a {@link ICSVFormat}.
 *
 * The parser works record wise. It is not possible to go back, once a record has been parsed from the input stream.
 *
 * <h2>Creating instances</h2>
 * <p>
 * There are several static factory methods that can be used to create instances for various types of resources:
 * </p>
 * <ul>
 *     <li>{@link #parse(java.io.File, Charset, ICSVFormat)}</li>
 *     <li>{@link #parse(String, ICSVFormat)}</li>
 *     <li>{@link #parse(java.net.URL, java.nio.charset.Charset, ICSVFormat)}</li>
 * </ul>
 * <p>
 * Alternatively parsers can also be created by passing a {@link Reader} directly to the sole constructor.
 *
 *
 * <h2>Parsing record wise</h2>
 * <p>
 * To parse a CSV input from a file, you write:
 * </p>
 *
 * <pre>
 * File csvData = new File(&quot;/path/to/csv&quot;);
 * CSVParser parser = CSVParser.parse(csvData, ICSVFormat.RFC4180);
 * for (CSVRecord csvRecord : parser) {
 *     ...
 * }
 * </pre>
 *
 * <p>
 * This will read the parse the contents of the file using the
 * <a href="http://tools.ietf.org/html/rfc4180" target="_blank">RFC 4180</a> format.
 * </p>
 *
 * <p>
 * To parse CSV input in a format like Excel, you write:
 * </p>
 *
 * <pre>
 * CSVParser parser = CSVParser.parse(csvData, ICSVFormat.EXCEL);
 * for (CSVRecord csvRecord : parser) {
 *     ...
 * }
 * </pre>
 *
 * <p>
 * If the predefined formats don't match the format at hands, custom formats can be defined. More information about
 * customising ICSVFormats is available in {@link ICSVFormat ICSVFormat Javadoc}.
 * </p>
 *
 * <h2>Parsing into memory</h2>
 * <p>
 * If parsing record wise is not desired, the contents of the input can be read completely into memory.
 * </p>
 *
 * <pre>
 * Reader in = new StringReader(&quot;a;b\nc;d&quot;);
 * CSVParser parser = new CSVParser(in, ICSVFormat.EXCEL);
 * List&lt;CSVRecord&gt; list = parser.getRecords();
 * </pre>
 *
 * <p>
 * There are two constraints that have to be kept in mind:
 * </p>
 *
 * <ol>
 *     <li>Parsing into memory starts at the current position of the parser. If you have already parsed records from
 *     the input, those records will not end up in the in memory representation of your CSV data.</li>
 *     <li>Parsing into memory may consume a lot of system resources depending on the input. For example if you're
 *     parsing a 150MB file of CSV data the contents will be read completely into memory.</li>
 * </ol>
 *
 * <h2>Notes</h2>
 * <p>
 * Internal parser state is completely covered by the format and the reader-state.
 * </p>
 *
 * @see <a href="package-summary.html">package documentation for more details</a>
 */
public class CSVParser extends BaseCSVParser {

    /**
     * Creates a parser for the given {@link File}.
     *
     * @param file
     *            a CSV file. Must not be null.
     * @param charset
     *            The Charset to decode the given file.
     * @param format
     *            the ICSVFormat used for CSV parsing. Must not be null.
     * @return a new parser
     * @throws IllegalArgumentException
     *             If the parameters of the format are inconsistent or if either file or format are null.
     * @throws IOException
     *             If an I/O error occurs
     */
    public static ICSVParser parse(final File file, final Charset charset, final ICSVFormat format) throws IOException {
        Objects.requireNonNull(file, "file");
        return parse(file.toPath(), charset, format);
    }

    /**
     * Creates a CSV parser using the given {@link ICSVFormat}.
     *
     * <p>
     * If you do not read all records from the given {@code reader}, you should call {@link #close()} on the parser,
     * unless you close the {@code reader}.
     * </p>
     *
     * @param inputStream
     *            an InputStream containing CSV-formatted input. Must not be null.
     * @param charset
     *            The Charset to decode the given file.
     * @param format
     *            the ICSVFormat used for CSV parsing. Must not be null.
     * @return a new CSVParser configured with the given reader and format.
     * @throws IllegalArgumentException
     *             If the parameters of the format are inconsistent or if either reader or format are null.
     * @throws IOException
     *             If there is a problem reading the header or skipping the first record
     * @since 1.5
     */
    @SuppressWarnings("resource")
    public static ICSVParser parse(final InputStream inputStream, final Charset charset, final ICSVFormat format)
            throws IOException {
        Objects.requireNonNull(inputStream, "inputStream");
        Objects.requireNonNull(format, "format");
        return parse(new InputStreamReader(inputStream, charset), format);
    }

    /**
     * Creates and returns a parser for the given {@link Path}, which the caller MUST close.
     *
     * @param path
     *            a CSV file. Must not be null.
     * @param charset
     *            The Charset to decode the given file.
     * @param format
     *            the ICSVFormat used for CSV parsing. Must not be null.
     * @return a new parser
     * @throws IllegalArgumentException
     *             If the parameters of the format are inconsistent or if either file or format are null.
     * @throws IOException
     *             If an I/O error occurs
     * @since 1.5
     */
    @SuppressWarnings("resource")
    public static ICSVParser parse(final Path path, final Charset charset, final ICSVFormat format) throws IOException {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(format, "format");
        return parse(Files.newInputStream(path), charset, format);
    }

    /**
     * Creates a CSV parser using the given {@link ICSVFormat}
     *
     * <p>
     * If you do not read all records from the given {@code reader}, you should call {@link #close()} on the parser,
     * unless you close the {@code reader}.
     * </p>
     *
     * @param reader
     *            a Reader containing CSV-formatted input. Must not be null.
     * @param format
     *            the ICSVFormat used for CSV parsing. Must not be null.
     * @return a new CSVParser configured with the given reader and format.
     * @throws IllegalArgumentException
     *             If the parameters of the format are inconsistent or if either reader or format are null.
     * @throws IOException
     *             If there is a problem reading the header or skipping the first record
     * @since 1.5
     */
    public static ICSVParser parse(final Reader reader, final ICSVFormat format) throws IOException {
        return new CSVParser(reader, format);
    }

    // the following objects are shared to reduce garbage

    /**
     * Creates a parser for the given {@link String}.
     *
     * @param string
     *            a CSV string. Must not be null.
     * @param format
     *            the ICSVFormat used for CSV parsing. Must not be null.
     * @return a new parser
     * @throws IllegalArgumentException
     *             If the parameters of the format are inconsistent or if either string or format are null.
     * @throws IOException
     *             If an I/O error occurs
     */
    public static CSVParser parse(final String string, final ICSVFormat format) throws IOException {
        Objects.requireNonNull(string, "string");
        Objects.requireNonNull(format, "format");

        return new CSVParser(new StringReader(string), format);
    }

    /**
     * Creates and returns a parser for the given URL, which the caller MUST close.
     *
     * <p>
     * If you do not read all records from the given {@code url}, you should call {@link #close()} on the parser, unless
     * you close the {@code url}.
     * </p>
     *
     * @param url
     *            a URL. Must not be null.
     * @param charset
     *            the charset for the resource. Must not be null.
     * @param format
     *            the ICSVFormat used for CSV parsing. Must not be null.
     * @return a new parser
     * @throws IllegalArgumentException
     *             If the parameters of the format are inconsistent or if either url, charset or format are null.
     * @throws IOException
     *             If an I/O error occurs
     */
    @SuppressWarnings("resource")
    public static ICSVParser parse(final URL url, final Charset charset, final ICSVFormat format) throws IOException {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(charset, "charset");
        Objects.requireNonNull(format, "format");

        return new CSVParser(new InputStreamReader(url.openStream(), charset), format);
    }

    /**
     * Customized CSV parser using the given {@link ICSVFormat}
     *
     * <p>
     * If you do not read all records from the given {@code reader}, you should call {@link #close()} on the parser,
     * unless you close the {@code reader}.
     * </p>
     *
     * @param reader
     *            a Reader containing CSV-formatted input. Must not be null.
     * @param format
     *            the ICSVFormat used for CSV parsing. Must not be null.
     * @throws IllegalArgumentException
     *             If the parameters of the format are inconsistent or if either reader or format are null.
     * @throws IOException
     *             If there is a problem reading the header or skipping the first record
     */
    public CSVParser(final Reader reader, final ICSVFormat format) throws IOException {
        this(reader, format, 0, 1);
    }

    /**
     * Customized CSV parser using the given {@link ICSVFormat}
     *
     * <p>
     * If you do not read all records from the given {@code reader}, you should call {@link #close()} on the parser,
     * unless you close the {@code reader}.
     * </p>
     *
     * @param reader
     *            a Reader containing CSV-formatted input. Must not be null.
     * @param format
     *            the ICSVFormat used for CSV parsing. Must not be null.
     * @param characterOffset
     *            Lexer offset when the parser does not start parsing at the beginning of the source.
     * @param recordNumber
     *            The next record number to assign
     * @throws IllegalArgumentException
     *             If the parameters of the format are inconsistent or if either reader or format are null.
     * @throws IOException
     *             If there is a problem reading the header or skipping the first record
     * @since 1.1
     */
    @SuppressWarnings("resource")
    public CSVParser(final Reader reader, final ICSVFormat format, final long characterOffset, final long recordNumber)
        throws IOException {
        super(format, recordNumber, characterOffset, new Lexer(format, new ExtendedBufferedReader(reader)));
        Objects.requireNonNull(reader, "reader");
        Objects.requireNonNull(format, "format");
    }
}
