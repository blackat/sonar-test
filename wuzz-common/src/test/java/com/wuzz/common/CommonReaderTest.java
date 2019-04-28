package com.wuzz.common;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommonReaderTest {

    private CommonReader commonReaderSUT;

    @Before
    public void setUp() {
        commonReaderSUT = new CommonReader();
    }

    @Test
    public void readMe() {
        commonReaderSUT.readMe();

        assertTrue(true);
    }
}