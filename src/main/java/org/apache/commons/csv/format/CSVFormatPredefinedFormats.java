package org.apache.commons.csv.format;

/**
 * Predefines formats.
 *
 * @since 1.2
 */
public enum CSVFormatPredefinedFormats {

    /**
     * @see CSVFormat#DEFAULT
     */
    Default(CSVFormat.DEFAULT),

    /**
     * @see CSVFormat#EXCEL
     */
    Excel(CSVFormat.EXCEL),

    /**
     * @see CSVFormat#INFORMIX_UNLOAD
     * @since 1.3
     */
    InformixUnload(CSVFormat.INFORMIX_UNLOAD),

    /**
     * @see CSVFormat#INFORMIX_UNLOAD_CSV
     * @since 1.3
     */
    InformixUnloadCsv(CSVFormat.INFORMIX_UNLOAD_CSV),

    /**
     * @see CSVFormat#MONGODB_CSV
     * @since 1.7
     */
    MongoDBCsv(CSVFormat.MONGODB_CSV),

    /**
     * @see CSVFormat#MONGODB_TSV
     * @since 1.7
     */
    MongoDBTsv(CSVFormat.MONGODB_TSV),

    /**
     * @see CSVFormat#MYSQL
     */
    MySQL(CSVFormat.MYSQL),

    /**
     * @see CSVFormat#ORACLE
     */
    Oracle(CSVFormat.ORACLE),

    /**
     * @see CSVFormat#POSTGRESQL_CSV
     * @since 1.5
     */
    PostgreSQLCsv(CSVFormat.POSTGRESQL_CSV),

    /**
     * @see CSVFormat#POSTGRESQL_CSV
     */
    PostgreSQLText(CSVFormat.POSTGRESQL_TEXT),

    /**
     * @see CSVFormat#RFC4180
     */
    RFC4180(CSVFormat.RFC4180),

    /**
     * @see CSVFormat#TDF
     */
    TDF(CSVFormat.TDF);

    private final CSVFormat format;

    CSVFormatPredefinedFormats(final CSVFormat format) {
        this.format = format;
    }

    /**
     * Gets the format.
     *
     * @return the format.
     */
    public CSVFormat getFormat() {
        return format;
    }
}
