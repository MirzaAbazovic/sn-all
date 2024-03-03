/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2011 12:00:37
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.common.service.HistoryService.IoArchiveTreeAndAnlagenList;

/**
 * Dialog zur Darstellung der WITA-History anhand der Vertragsnummer
 */
public class HistoryByVertragsnummerDialog extends HistoryDialog {

    private static final long serialVersionUID = -3671070565495229312L;

    private final String vertragsNummer;

    public HistoryByVertragsnummerDialog(String vertragsNummer) {
        super(RESOURCE);
        this.vertragsNummer = vertragsNummer;
        initDialog();
    }

    @Override
    protected IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenList() throws FindException {
        return historyService.loadIoArchiveTreeAndAnlagenListForVertragsnummer(vertragsNummer);
    }
}
