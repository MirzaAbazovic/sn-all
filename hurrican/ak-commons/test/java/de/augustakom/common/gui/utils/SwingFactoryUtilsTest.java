/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.13
 */
package de.augustakom.common.gui.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
@Test(groups = { "unit" })
public class SwingFactoryUtilsTest {

    public void convertMultilineTextToHtml() {
        Assert.assertEquals(SwingFactoryUtils.convertMultilineTextToHtml("test1 test2"), "test1 test2");
        Assert.assertEquals(SwingFactoryUtils.convertMultilineTextToHtml("test1 {linebreak}test2"), "<html><div align=\"center\">test1 </div><div align=\"center\">test2</div></html>");
        Assert.assertEquals(SwingFactoryUtils.convertMultilineTextToHtml("Kündigung ohne{linebreak}Rufnummerportierung"), "<html><div align=\"center\">Kündigung ohne</div><div align=\"center\">Rufnummerportierung</div></html>");
        Assert.assertEquals(SwingFactoryUtils.convertMultilineTextToHtml("test1 {linebreak}test2{linebreak}test3"), "<html><div align=\"center\">test1 </div><div align=\"center\">test2</div><div align=\"center\">test3</div></html>");
        Assert.assertEquals(SwingFactoryUtils.convertMultilineTextToHtml("{linebreak}test1 {linebreak}{linebreak}test2"), "<html><div align=\"center\"></div><div align=\"center\">test1 </div><div align=\"center\"></div><div align=\"center\">test2</div></html>");
    }

    public void formatStringTextWithThousandsSeparators() {
        Assert.assertEquals(SwingFactoryUtils.formatStringTextWithThousandsSeparators("10000"), "10.000");
        Assert.assertEquals(SwingFactoryUtils.formatStringTextWithThousandsSeparators("100000 kbit/s"), "100.000 kbit/s");
        Assert.assertEquals(SwingFactoryUtils.formatStringTextWithThousandsSeparators("100000 kbit/s / 10000 kbit/s"), "100.000 kbit/s / 10.000 kbit/s");
        Assert.assertNull(SwingFactoryUtils.formatStringTextWithThousandsSeparators(null));
    }

}
