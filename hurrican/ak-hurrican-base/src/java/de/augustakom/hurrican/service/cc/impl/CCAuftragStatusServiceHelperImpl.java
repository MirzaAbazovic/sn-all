/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusServiceHelper;
import de.augustakom.hurrican.service.cc.ProduktService;

@CcTxRequired
public class CCAuftragStatusServiceHelperImpl implements CCAuftragStatusServiceHelper {

    private static final Logger LOGGER = Logger.getLogger(CCAuftragStatusServiceHelperImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;

    @Resource(name = "auftragDatenDAO")
    private AuftragDatenDAO auftragDatenDAO;

    @Resource(name = "auftragTechnikDAO")
    private AuftragTechnikDAO auftragTechnikDAO;

    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;

    @Override
    @CcTxRequiresNew
    public void kuendigeAuftragReqNew(Long auftragId, Date kuendigungsDatum, AKUser user) throws StoreException {
        kuendigeAuftrag(auftragId, kuendigungsDatum, user);
    }

    @Override
    public void kuendigeAuftragReq(Long auftragId, Date kuendigungsDatum, AKUser user) throws StoreException {
        kuendigeAuftrag(auftragId, kuendigungsDatum, user);
    }

    private void kuendigeAuftrag(Long auftragId, Date kuendigungsDatum, AKUser user) throws StoreException {
        if (auftragId == null) {
            throw new StoreException("Es wurde keine Auftrags-ID angegeben! Auftrag kann nicht gekündigt werden.");
        }
        if (kuendigungsDatum == null) {
            throw new StoreException("Kuendigungsdatum wurde nicht angegeben! Auftrag kann nicht gekündigt werden.");
        }

        try {
            AuftragDaten ad = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
            AuftragTechnik at = ccAuftragService.findAuftragTechnikByAuftragIdTx(auftragId);
            if ((ad != null) && (at != null)) {
                final Produkt produkt = produktService.findProdukt(ad.getProdId());
                final Long kuendigungStatusId = produkt.getKuendigungStatusId();
                ad.setStatusId(kuendigungStatusId == null ? AuftragStatus.KUENDIGUNG_ERFASSEN : kuendigungStatusId);
                ad.setKuendigung(kuendigungsDatum);
                if (user != null) {
                    ad.setBearbeiter(user.getName());
                }
                auftragDatenDAO.store(ad);

                at.setAuftragsart(BAVerlaufAnlass.KUENDIGUNG);
                at.setProjectResponsibleUserId(null);
                auftragTechnikDAO.store(at);
            }
            else {
                String id = auftragId.toString();
                StringBuilder msg = new StringBuilder();
                msg.append("Der Auftrag mit der ID {0} konnte nicht gekündigt werden, ");
                msg.append("da keine entsprechenden Daten gefunden wurden.");
                throw new StoreException(StringTools.formatString(msg.toString(), new Object[] { id }, null));
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            String id = auftragId.toString();
            throw new StoreException(StoreException.UNABLE_TO_CANCEL_CC_AUFTRAG, new Object[] { id }, e);
        }
    }

}
