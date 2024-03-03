/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2004 10:04:20
 */
package de.augustakom.hurrican.gui.base;

import javax.swing.text.*;
import org.apache.log4j.Logger;

/**
 * Mask-Formatter fuer eine Kunden-No. <br> Die Kundennummer muss immer genau 9 Ziffern besitzen. Standardmaessig werden
 * die Ziffern mit '0' aufgefuellt.
 *
 *
 */
public class KNoMaskFormatter extends MaskFormatter {

    private static final Logger LOGGER = Logger.getLogger(KNoMaskFormatter.class);

    public KNoMaskFormatter() {
        super();
        try {
            setMask("#########");
            setPlaceholderCharacter('0');
            setOverwriteMode(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}

