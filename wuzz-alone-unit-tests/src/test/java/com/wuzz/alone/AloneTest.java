package com.wuzz.alone;

import com.wuzz.common.CommonReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AloneTest {

    private CommonReader commonReader;

    @Before
    public void setUp() {
        commonReader = new CommonReader();
    }

    @Test
    public void aloneTest() {
        commonReader.listenMe();

        assertTrue(true);
    }
}
