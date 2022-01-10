package org.apache.commons.csv.format;

import org.junit.jupiter.api.Test;

public class JiraCsv236Test {

    @Test
    public void testJiraCsv236() {
        new CSVFormatBuilder().setAllowDuplicateHeaderNames(true).setHeaders("CC","VV","VV").build();
    }

}
