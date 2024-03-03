/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.13
 */
package de.augustakom.common.gui.swing;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AKJLabelTest {
    @Test
    public void testConvertCurrentTextToMultiline() throws Exception {
        for (AKJLabel testling : Arrays.asList(new AKJLabel("test\\ntest"), new AKJLabel("test\ntest"))) {
            testling.convertCurrentTextToMultiline();
            Assert.assertEquals(testling.getText(), "<html>test<br/>test</html>");
        }
    }

    @Test
    public void testConvertCurrentTextToMultilineCountOF() throws Exception {
        for (AKJLabel testling : Arrays.asList(new AKJLabel("testtest"), new AKJLabel("test test"))) {
            testling.convertCurrentTextToMultiline(4);
            Assert.assertEquals(testling.getText(), "<html>test<br/>test</html>");
        }
        AKJLabel testling = new AKJLabel("bla bala bla bal");
        testling.convertCurrentTextToMultiline(30);
        Assert.assertEquals(testling.getText(), "<html>bla bala bla bal</html>");
    }
}
