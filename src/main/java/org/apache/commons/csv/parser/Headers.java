package org.apache.commons.csv.parser;

import java.util.List;
import java.util.Map;

/**
 * Header information based on name and position.
 */
final class Headers {
    /**
     * Header column positions (0-based)
     */
    final Map<String, Integer> headerMap;

    /**
     * Header names in column order
     */
    final List<String> headerNames;

    Headers(final Map<String, Integer> headerMap, final List<String> headerNames) {
        this.headerMap = headerMap;
        this.headerNames = headerNames;
    }
}
