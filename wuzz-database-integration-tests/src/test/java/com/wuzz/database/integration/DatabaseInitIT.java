package com.wuzz.database.integration;

import com.wuzz.common.CommonReader;
import com.wuzz.database.Initializer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DatabaseInitIT {

    private CommonReader commonReaderSUT;

    @Before
    public void setUp() {
        commonReaderSUT = new CommonReader();
    }

    @Test
    public void waitMeGoldenPath() {
        commonReaderSUT.waitMe();

        assertTrue(true);
    }
}
