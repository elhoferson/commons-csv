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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.apache.commons.csv.printer.CSVPrinter;
import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.QuoteMode;
import org.junit.jupiter.api.Test;

/**
 * Tests [CSV-263] Print from Reader with embedded quotes generates incorrect output.
 */
public class JiraCsv263Test {

    @Test
    public void testPrintFromReaderWithQuotes() throws IOException {
        // @formatter:off

        CSVFormat csvFormat = CSVFormatPredefinedFormats.RFC4180.getFormat();
        csvFormat.setDelimiter(",");
        csvFormat.setQuoteCharacter('"');
        csvFormat.setEscapeCharacter('?');
        csvFormat.setQuoteMode(QuoteMode.NON_NUMERIC);

        // @formatter:on
        final StringBuilder out = new StringBuilder();

        final Reader atStartOnly = new StringReader("\"a,b,c\r\nx,y,z");
        CSVPrinter csvPrinter = new CSVPrinter(out, csvFormat);
        csvPrinter.print(atStartOnly, true);
        assertEquals("\"\"\"a,b,c\r\nx,y,z\"", out.toString());

        final Reader atEndOnly = new StringReader("a,b,c\r\nx,y,z\"");
        out.setLength(0);
        csvPrinter.print(atEndOnly, true);
        assertEquals("\"a,b,c\r\nx,y,z\"\"\"", out.toString());

        final Reader atBeginEnd = new StringReader("\"a,b,c\r\nx,y,z\"");
        out.setLength(0);
        csvPrinter.print(atBeginEnd, true);
        assertEquals("\"\"\"a,b,c\r\nx,y,z\"\"\"", out.toString());

        final Reader embeddedBeginMiddle = new StringReader("\"a\",b,c\r\nx,\"y\",z");
        out.setLength(0);
        csvPrinter.print(embeddedBeginMiddle, true);
        assertEquals("\"\"\"a\"\",b,c\r\nx,\"\"y\"\",z\"", out.toString());

        final Reader embeddedMiddleEnd = new StringReader("a,\"b\",c\r\nx,y,\"z\"");
        out.setLength(0);
        csvPrinter.print(embeddedMiddleEnd, true);
        assertEquals("\"a,\"\"b\"\",c\r\nx,y,\"\"z\"\"\"", out.toString());

        final Reader nested = new StringReader("a,\"b \"and\" c\",d");
        out.setLength(0);
        csvPrinter.print(nested, true);
        assertEquals("\"a,\"\"b \"\"and\"\" c\"\",d\"", out.toString());
    }

}
