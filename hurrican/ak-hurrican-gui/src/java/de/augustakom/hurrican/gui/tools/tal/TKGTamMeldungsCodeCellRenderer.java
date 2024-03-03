/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2016
 */
package de.augustakom.hurrican.gui.tools.tal;

import javax.swing.table.*;

import de.mnet.wita.config.WitaConstants;

public class TKGTamMeldungsCodeCellRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        Object myValue = value;
        if (value instanceof String) {
            myValue = getDisplayNameOfTamArt(value);

            String tooltip;
            if (WitaConstants.DISPLAYNAME_TA.equals((myValue))) {
                tooltip = "TA-Fall: (T)ermin (A)m nächsten Werktag. Beim TA – Fall ist keine Aktion für das Routing erforderlich. Das\n"
                        + " Routing erfolgt zum Tag der ursprünglichen Schaltung.";
            }
            else {
                tooltip = "Die Schaltung konnte nicht am Ursprungs-VLT durchgeführt werden";
            }

            tooltip = String.format("%s. Meldungscode = %s.", tooltip, value);
            setToolTipText(tooltip);
        }
        super.setValue(myValue);
    }

    private String getDisplayNameOfTamArt(Object meldungscode) {
        return isTAFall(meldungscode) ? WitaConstants.DISPLAYNAME_TA : WitaConstants.DISPLAYNAME_NICHT_TA;
    }

    private boolean isTAFall(Object meldungsCode) {
        return WitaConstants.MELDUNGSCODE_6012.equals(meldungsCode);
    }
}

