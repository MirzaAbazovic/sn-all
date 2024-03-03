/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.04.2007 10:36:07
 */
package de.augustakom.hurrican.service.cc.impl.command.archiv;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Gibt das Schlagwort "KUNDENVERTRAG" zurueck. Die sist der eindeutige Qualifizierer im Archivsystem ScanView
 *
 *
 */
public class SVGetKundenVertragNoCommand extends AbstractArchivCommand {

    private static final Logger LOGGER = Logger.getLogger(SVGetKundeNoCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            PhysikService pService = getCCService(PhysikService.class);
            VerbindungsBezeichnung verbindungsBezeichnung = pService.findVerbindungsBezeichnungByAuftragId((Long) getPreparedValue(AbstractArchivCommand.AUFTRAG_ID));
            if (verbindungsBezeichnung != null) {
                List<String> retVal = new ArrayList<String>();
                retVal.add(verbindungsBezeichnung.getVbz() + "_" + getPreparedValue(KUNDE__NO));
                return retVal;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(FindException._UNEXPECTED_ERROR, e);
        }
    }
}


