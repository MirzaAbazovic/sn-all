/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.2009 17:15:54
 */
package de.augustakom.hurrican.model.reporting;

import static org.testng.Assert.*;

import java.io.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class ReportRequestTest extends BaseTest {

    public void testGetFilePathForCurrentPlatform() {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setFile("Archiv/test/bla.pdf");
        String correctedFilePath = reportRequest.getFilePathForCurrentPlatform();

        if ("/".equals(File.separator)) {
            assertFalse(correctedFilePath.contains("\\"));
        }
        else if ("\\".equals(File.separator)) {
            assertFalse(correctedFilePath.contains("/"));
        }
    }
}
