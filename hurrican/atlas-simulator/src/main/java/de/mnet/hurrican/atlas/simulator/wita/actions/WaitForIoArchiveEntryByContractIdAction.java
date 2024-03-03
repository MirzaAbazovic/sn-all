/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.10.14
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import java.util.*;

import de.mnet.common.service.HistoryService;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.IoArchive;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;

/**
 * Test action checks for IOArchive entry in database. Action searches for contract id and meldungs type.
 *
 *
 */
public class WaitForIoArchiveEntryByContractIdAction extends WaitForIoArchiveEntryAction {

    public WaitForIoArchiveEntryByContractIdAction(HistoryService historyService, MeldungsType meldungsType, Long numberOfEntries) {
        super("waitForIoArchiveByContractId", historyService, meldungsType, numberOfEntries);
    }

    @Override
    protected String getIdVariableName() {
        return WitaLineOrderVariableNames.CONTRACT_ID;
    }

    @Override
    protected Collection<IoArchive> findIoArchiveEntries(String id) {
        return historyService.findIoArchivesForVertragsnummer(id);
    }

}
