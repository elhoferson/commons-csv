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

package org.apache.commons.csv.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.csv.format.CSVFormat;
import org.apache.commons.csv.format.CSVFormatPredefinedFormats;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CSVFormatPredefinedFormats}.
 */
public class CSVFormatPredefinedFormatsTest {

    private void test(final CSVFormat format, final String enumName) {
        assertEquals(format, CSVFormatPredefinedFormats.valueOf(enumName).getFormat());
        assertEquals(format, CSVFormat.valueOf(enumName));
    }

    @Test
    public void testDefault() {
        test(CSVFormatPredefinedFormats.Default.getFormat(), "Default");
    }

    @Test
    public void testExcel() {
        test(CSVFormatPredefinedFormats.Excel.getFormat(), "Excel");
    }

    @Test
    public void testMongoDbCsv() {
        test(CSVFormatPredefinedFormats.MongoDBCsv.getFormat(), "MongoDBCsv");
    }

    @Test
    public void testMongoDbTsv() {
        test(CSVFormatPredefinedFormats.MongoDBTsv.getFormat(), "MongoDBTsv");
    }

    @Test
    public void testMySQL() {
        test(CSVFormatPredefinedFormats.MySQL.getFormat(), "MySQL");
    }

    @Test
    public void testOracle() {
        test(CSVFormatPredefinedFormats.Oracle.getFormat(), "Oracle");
    }

    @Test
    public void testPostgreSqlCsv() {
        test(CSVFormatPredefinedFormats.PostgreSQLCsv.getFormat(), "PostgreSQLCsv");
    }

    @Test
    public void testPostgreSqlText() {
        test(CSVFormatPredefinedFormats.PostgreSQLText.getFormat(), "PostgreSQLText");
    }

    @Test
    public void testRFC4180() {
        test(CSVFormatPredefinedFormats.RFC4180.getFormat(), "RFC4180");
    }

    @Test
    public void testTDF() {
        test(CSVFormatPredefinedFormats.TDF.getFormat(), "TDF");
    }
}
