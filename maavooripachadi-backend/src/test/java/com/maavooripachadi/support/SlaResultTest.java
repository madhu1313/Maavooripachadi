package com.maavooripachadi.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SlaResultTest {

    @Test
    void exposesEnumValues() {
        assertArrayEquals(new SlaResult[]{SlaResult.MET, SlaResult.BREACHED}, SlaResult.values());
        assertEquals(SlaResult.MET, SlaResult.valueOf("MET"));
    }
}
