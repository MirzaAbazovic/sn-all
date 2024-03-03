/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004 08:35:39
 */
package de.augustakom.common.gui.swing;


/**
 *
 */
public class AKJDefaultErrorDialogSample {

    public static void main(String[] args) {
        Exception e1 = new IllegalArgumentException("Test-Exception message 1");
        Exception e2 = new Exception("Test-Exception message 2", e1);

        e2.printStackTrace();

        MessageHelper.showErrorDialog(null, e2);

        MessageHelper.showInfoDialog(null, "Test-Msg. Param0: {0}, Param1: {1}", new Object[] { "null", "eins" });

        //System.exit(0);
    }
}
