package org.apache.commons.csv.record;

import java.util.List;
import java.util.Map;

/**
 * Header information based on name and position.
 */
public final class Headers {
    private final Map<String, Integer> headerMap;

    private final List<String> headerNames;

    public Headers(final Map<String, Integer> headerMap, final List<String> headerNames) {
        this.headerMap = headerMap;
        this.headerNames = headerNames;
    }

    /**
     * Header column positions (0-based)
     */
    public Map<String, Integer> getHeaderMap() {
        return headerMap;
    }

    /**
     * Header names in column order
     */
    public List<String> getHeaderNames() {
        return headerNames;
    }
}
