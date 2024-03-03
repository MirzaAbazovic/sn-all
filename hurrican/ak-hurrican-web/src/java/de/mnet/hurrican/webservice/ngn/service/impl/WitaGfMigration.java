/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.2016
 */
package de.mnet.hurrican.webservice.ngn.service.impl;

import static de.augustakom.hurrican.model.cc.tal.CBVorgang.*;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.hurrican.webservice.ngn.service.PortierungskennungMigrationException;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.WitaCBVorgang;

public class WitaGfMigration {
    private static final Logger LOGGER = Logger.getLogger(WitaGfMigration .class);

    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private CarrierService carrierService;

    @Autowired
    private WbciWitaServiceFacade wbciWitaServiceFacade;

    public void performMigrationWitaGf(AuftragDaten auftragDaten) throws PortierungskennungMigrationException {
        try {
            final Carrier mnetNgnCarrier = carrierService.findCarrier(Carrier.ID_MNET_NGN);
            final String mnetNgnElTalEmpfId = mnetNgnCarrier.getElTalEmpfId();

            final List<WitaCBVorgang> cbVorgaengeNotTransfered = wbciWitaServiceFacade.findWitaCbVorgaengeByAuftrag(auftragDaten.getAuftragId())
                    .stream()
                    .filter(cb -> cb.getStatus() < STATUS_TRANSFERRED)
                    .collect(Collectors.toList());

            for (WitaCBVorgang witaCBVorgang : cbVorgaengeNotTransfered) {
                final List<Meldung<?>> allMeldungen = mwfEntityDao.findAllMeldungen(witaCBVorgang.getBusinessKey());
                for (Meldung meldung : allMeldungen) {
                    final RufnummernPortierung rufnummernPortierung = meldung.getRufnummernPortierung();
                    if (rufnummernPortierung != null) {
                        if (!StringUtils.equals(mnetNgnElTalEmpfId, rufnummernPortierung.getPortierungsKenner())) {
                            rufnummernPortierung.setPortierungsKenner(mnetNgnElTalEmpfId);
                            mwfEntityDao.store(rufnummernPortierung);
                            LOGGER.info(String.format("Wita CB Vorgang [%d] WITA-Meldung [%d] wurde migriert",
                                    witaCBVorgang.getId(), meldung.getId()));
                        }
                        else {
                            LOGGER.warn(String.format("Wita CB Vorgang [%d] WITA-Meldung [%d] ist schon migriert",
                                    witaCBVorgang.getId(), meldung.getId()));
                        }
                    }
                    else {
                        LOGGER.warn(String.format("Wita CB Vorgang [%d] WITA-Meldung [%d] hat kein RufnummernPortierung",
                                witaCBVorgang.getId(), meldung.getId()));
                    }
                }
            }
        }
        catch (FindException e) {
            throw new PortierungskennungMigrationException("Fehler bei der Migration von dem WITA CB Vorgang aufgetreten", e);
        }

    }

}

