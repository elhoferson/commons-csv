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
package org.apache.commons.csv.issues;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.format.QuoteMode;
import org.apache.commons.csv.parser.CSVParser;
import org.apache.commons.csv.parser.ICSVParser;
import org.apache.commons.csv.record.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Add more tests about null value.
 * <p>
 * QuoteMode:ALL_NON_NULL (Quotes all non-null fields, null will not be quoted but not null will be quoted). when
 * withNullString("NULL"), NULL String value ("NULL") and null value (null) will be formatted as '"NULL",NULL'. So it
 * also should be parsed as NULL String value and null value (["NULL", null]), It should be distinguish in parsing. And
 * when don't set nullString in CSVFormat, String '"",' should be parsed as "" and null (["", null]) according to null
 * will not be quoted but not null will be quoted. QuoteMode:NON_NUMERIC, same as ALL_NON_NULL.
 * </p>
 * <p>
 * This can solve the problem of distinguishing between empty string columns and absent value columns which just like
 * Jira CSV-253 to a certain extent.
 * </p>
 */
public class JiraCsv93Test {
    private static Object[] objects1 = {"abc", "", null, "a,b,c", 123};

    private static Object[] objects2 = {"abc", "NULL", null, "a,b,c", 123};

    private void every(final CSVFormat csvFormat, final Object[] objects, final String format, final String[] data)
        throws IOException {
        final String source = csvFormat.format(objects);
        assertEquals(format, csvFormat.format(objects));
        try (final ICSVParser ICSVParser = new CSVParser(new StringReader(source), csvFormat)) {
            final CSVRecord csvRecord = ICSVParser.iterator().next();
            for (int i = 0; i < data.length; i++) {
                assertEquals(csvRecord.get(i), data[i]);
            }
        }
    }

    @Test
    public void testWithNotSetNullString() throws IOException {
        // @formatter:off
        CSVFormat format = CSVFormatPredefinedFormats.Default.getFormat();

        every(format,
                objects1,
                "abc,,,\"a,b,c\",123",
                new String[]{"abc", "", "", "a,b,c", "123"});

        CSVFormat format1 = format.copy();
        format1.setQuoteMode(QuoteMode.ALL);

        every(format1,
                objects1,
                "\"abc\",\"\",,\"a,b,c\",\"123\"",
                new String[]{"abc", "", "", "a,b,c", "123"});

        CSVFormat format2 = format.copy();
        format2.setQuoteMode(QuoteMode.ALL_NON_NULL);

        every(format2,
                objects1,
                "\"abc\",\"\",,\"a,b,c\",\"123\"",
                new String[]{"abc", "", null, "a,b,c", "123"});

        CSVFormat format3 = format.copy();
        format3.setQuoteMode(QuoteMode.MINIMAL);

        every(format3,
                objects1,
                "abc,,,\"a,b,c\",123",
                new String[]{"abc", "", "", "a,b,c", "123"});

        CSVFormat format4 = format.copy();
        format4.setEscapeCharacter('?');
        format4.setQuoteMode(QuoteMode.NONE);

        every(format4,
                objects1,
                "abc,,,a?,b?,c,123",
                new String[]{"abc", "", "", "a,b,c", "123"});

        CSVFormat format5 = format.copy();
        format5.setQuoteMode(QuoteMode.NON_NUMERIC);

        every(format5,
                objects1,
                "\"abc\",\"\",,\"a,b,c\",123",
                new String[]{"abc", "", null, "a,b,c", "123"});
        // @formatter:on
    }

    @Test
    public void testWithSetNullStringEmptyString() throws IOException {
        // @formatter:off

        CSVFormat formatEmptyStr = CSVFormatPredefinedFormats.Default.getFormat();
        formatEmptyStr.setNullString("");

        every(formatEmptyStr,
                objects1,
                "abc,,,\"a,b,c\",123",
                new String[]{"abc", null, null, "a,b,c", "123"});

        CSVFormat formatEmptyStr1 = formatEmptyStr.copy();
        formatEmptyStr1.setQuoteMode(QuoteMode.ALL);

        every(formatEmptyStr1,
                objects1,
                "\"abc\",\"\",\"\",\"a,b,c\",\"123\"",
                new String[]{"abc", null, null, "a,b,c", "123"});

        CSVFormat formatEmptyStr2 = formatEmptyStr.copy();
        formatEmptyStr2.setQuoteMode(QuoteMode.ALL_NON_NULL);

        every(formatEmptyStr2,
                objects1,
                "\"abc\",\"\",,\"a,b,c\",\"123\"",
                new String[]{"abc", "", null, "a,b,c", "123"});

        CSVFormat formatEmptyStr3 = formatEmptyStr.copy();
        formatEmptyStr3.setQuoteMode(QuoteMode.MINIMAL);

        every(formatEmptyStr3,
                objects1,
                "abc,,,\"a,b,c\",123",
                new String[]{"abc", null, null, "a,b,c", "123"});

        CSVFormat formatEmptyStr4 = formatEmptyStr.copy();
        formatEmptyStr4.setQuoteMode(QuoteMode.NONE);
        formatEmptyStr4.setEscapeCharacter('?');

        every(formatEmptyStr4,
                objects1,
                "abc,,,a?,b?,c,123",
                new String[]{"abc", null, null, "a,b,c", "123"});

        CSVFormat formatEmptyStr5 = formatEmptyStr.copy();
        formatEmptyStr5.setQuoteMode(QuoteMode.NON_NUMERIC);

        every(formatEmptyStr5,
                objects1,
                "\"abc\",\"\",,\"a,b,c\",123",
                new String[]{"abc", "", null, "a,b,c", "123"});
        // @formatter:on
    }

    @Test
    public void testWithSetNullStringNULL() throws IOException {
        // @formatter:off

        CSVFormat formatNullStr = CSVFormatPredefinedFormats.Default.getFormat();
        formatNullStr.setNullString("NULL");

        every(formatNullStr,
                objects2,
                "abc,NULL,NULL,\"a,b,c\",123",
                new String[]{"abc", null, null, "a,b,c", "123"});

        CSVFormat formatNullStr1 = formatNullStr.copy();
        formatNullStr1.setQuoteMode(QuoteMode.ALL);

        every(formatNullStr1,
                objects2,
                "\"abc\",\"NULL\",\"NULL\",\"a,b,c\",\"123\"",
                new String[]{"abc", null, null, "a,b,c", "123"});

        CSVFormat formatNullStr2 = formatNullStr.copy();
        formatNullStr2.setQuoteMode(QuoteMode.ALL_NON_NULL);

        every(formatNullStr2,
                objects2,
                "\"abc\",\"NULL\",NULL,\"a,b,c\",\"123\"",
                new String[]{"abc", "NULL", null, "a,b,c", "123"});

        CSVFormat formatNullStr3 = formatNullStr.copy();
        formatNullStr3.setQuoteMode(QuoteMode.MINIMAL);

        every(formatNullStr3,
                objects2,
                "abc,NULL,NULL,\"a,b,c\",123",
                new String[]{"abc", null, null, "a,b,c", "123"});

        CSVFormat formatNullStr4 = formatNullStr.copy();
        formatNullStr4.setQuoteMode(QuoteMode.NONE);
        formatNullStr4.setEscapeCharacter('?');

        every(formatNullStr4,
                objects2,
                "abc,NULL,NULL,a?,b?,c,123",
                new String[]{"abc", null, null, "a,b,c", "123"});

        CSVFormat formatNullStr5 = formatNullStr.copy();
        formatNullStr5.setQuoteMode(QuoteMode.NON_NUMERIC);

        every(formatNullStr5,
                objects2,
                "\"abc\",\"NULL\",NULL,\"a,b,c\",123",
                new String[]{"abc", "NULL", null, "a,b,c", "123"});
        // @formatter:on
    }
}
