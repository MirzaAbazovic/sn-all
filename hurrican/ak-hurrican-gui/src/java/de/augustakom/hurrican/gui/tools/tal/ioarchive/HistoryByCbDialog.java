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
 * Dialog zur Darstellung der WITA- und WBCI-History anhand der Carrierbestellung und AuftragId
 */
public class HistoryByCbDialog extends HistoryDialog {

    private static final long serialVersionUID = -3671070565495229312L;

    private final Long cbId;
    private final Long auftragId;

    public HistoryByCbDialog(Long cbId, Long auftragId) {
        super(RESOURCE);
        this.cbId = cbId;
        this.auftragId = auftragId;
        initDialog();
    }

    @Override
    protected IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenList() throws FindException {
        return historyService.loadIoArchiveTreeAndAnlagenListFor(cbId, auftragId);
    }
}
