package com.submodule.alone;

import com.wuzz.common.CommonReader;
import org.junit.Before;
import org.junit.Test;

public class SubModuleAloneTest {

    private CommonReader commonReader;

    @Before
    public void setUp() {
        commonReader = new CommonReader();
    }

    @Test
    public void subModuleAloneTest() {
        commonReader.writeMe();
    }
}
