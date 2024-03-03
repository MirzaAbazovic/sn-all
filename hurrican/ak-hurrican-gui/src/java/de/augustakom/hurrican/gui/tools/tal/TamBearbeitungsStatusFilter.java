/**
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2016
 */
package de.augustakom.hurrican.gui.tools.tal;

import de.mnet.wita.model.TamUserTask;

public enum TamBearbeitungsStatusFilter {
    UNDEFINED(null),
    OFFEN(TamUserTask.TamBearbeitungsStatus.OFFEN),
    IN_BEARBEITUNG(TamUserTask.TamBearbeitungsStatus.IN_BEARBEITUNG),
    KUNDE_NICHT_ERREICHT(TamUserTask.TamBearbeitungsStatus.KUNDE_NICHT_ERREICHT),
    KUNDE_MELDET_SICH(TamUserTask.TamBearbeitungsStatus.KUNDE_MELDET_SICH),
    TV_60_TAGE(TamUserTask.TamBearbeitungsStatus.TV_60_TAGE),
    TV_30_TAGE(TamUserTask.TamBearbeitungsStatus.TV_30_TAGE),
    INFO_MAILED(TamUserTask.TamBearbeitungsStatus.INFO_MAILED),
    ;

    private final TamUserTask.TamBearbeitungsStatus status;

    TamBearbeitungsStatusFilter(TamUserTask.TamBearbeitungsStatus status) {
        this.status = status;
    }

    public String getDisplay() {
        if (status != null)
        {
            return status.getDisplay();
        }
        else {
            return "";
        }
    }
}
