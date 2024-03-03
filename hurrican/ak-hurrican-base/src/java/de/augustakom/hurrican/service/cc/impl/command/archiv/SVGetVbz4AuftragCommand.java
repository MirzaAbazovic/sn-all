/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2007 13:45:43
 */
package de.augustakom.hurrican.service.cc.impl.command.archiv;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * ermittel die der Auftrag_id zugehörige VerbindungsBezeichnung
 *
 *
 */
public class SVGetVbz4AuftragCommand extends AbstractArchivCommand {

    private static final Logger LOGGER = Logger.getLogger(SVGetVbz4AuftragCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            PhysikService pService = getCCService(PhysikService.class);
            VerbindungsBezeichnung verbindungsBezeichnung = pService.findVerbindungsBezeichnungByAuftragId((Long) getPreparedValue(AbstractArchivCommand.AUFTRAG_ID));
            if (verbindungsBezeichnung != null) {
                List<String> retVal = new ArrayList<String>();
                retVal.add(verbindungsBezeichnung.getVbz());
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


