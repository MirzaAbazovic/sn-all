/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2011 16:12:56
 */
package de.mnet.wita.message.builder;

import static org.testng.Assert.*;

import com.google.common.io.Resources;

import de.mnet.wita.message.common.Dateityp;

public enum TestAnlage {
    SIMPLE("simpleExcelTestAnlage.xls", Dateityp.EXCEL),
    LARGE("largeExcelTestAnlage.xls", Dateityp.EXCEL),
    TOO_LARGE("tooLargeExcelTestAnlage.xls", Dateityp.EXCEL);

    public final String resourceFileName;
    public final Dateityp dateityp;
    private byte[] inhalt;

    private TestAnlage(String resourceFileName, Dateityp dateityp) {
        this.resourceFileName = resourceFileName;
        this.dateityp = dateityp;
    }

    public byte[] getAnlageInhalt() {
        if (inhalt == null) {
            try {
                inhalt = Resources.toByteArray(Resources.getResource(resourceFileName));
            }
            catch (Exception e) {
                fail("could not determine content of TestAnlage " + this.name());
            }
        }
        return inhalt;
    }
}
