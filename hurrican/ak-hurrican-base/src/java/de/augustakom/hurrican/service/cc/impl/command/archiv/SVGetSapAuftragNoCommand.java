/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2007 14:31:46
 */
package de.augustakom.hurrican.service.cc.impl.command.archiv;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Ermittelt die dem Auftrag zugehörigen SAP-Auftragsnummern aus dem Billingsystem
 *
 *
 */
public class SVGetSapAuftragNoCommand extends AbstractArchivCommand {

    private static final Logger LOGGER = Logger.getLogger(SVGetSapAuftragNoCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            List<String> retVal = new ArrayList<String>();

            if (StringUtils.isBlank(getPreparedValue(ORDER__NO).toString())) {
                PhysikService pService = getCCService(PhysikService.class);
                VerbindungsBezeichnung verbindungsBezeichnung = pService.findVerbindungsBezeichnungByAuftragId((Long) getPreparedValue(AbstractArchivCommand.AUFTRAG_ID));
                if (verbindungsBezeichnung != null) {
                    retVal.add(verbindungsBezeichnung.getVbz());
                }
            }
            else {
                BillingAuftragService bAuftragService =
                        getBillingService(BillingAuftragService.class);
                BAuftrag bAuftrag = bAuftragService.findAuftrag((Long) getPreparedValue(AbstractArchivCommand.ORDER__NO));
                if (bAuftrag != null) {
                    retVal.add(bAuftrag.getSapId());
                }
            }
            return retVal;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(FindException._UNEXPECTED_ERROR, e);
        }
    }

}


