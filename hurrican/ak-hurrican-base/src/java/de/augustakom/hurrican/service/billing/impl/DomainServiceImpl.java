/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.2004 08:07:32
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.dao.billing.DomainDao;
import de.augustakom.hurrican.model.billing.view.IntDomain;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DomainService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Service-Implementierung von <code>DomainService</code>.
 *
 *
 */
@BillingTx
public class DomainServiceImpl extends DefaultBillingService implements DomainService {

    private static final Logger LOGGER = Logger.getLogger(DomainServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;

    @Autowired
    private DomainDao domainDao;

    @Override
    public List<IntDomain> findDomains4Auftrag(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) {
            return null;
        }
        List<Long> ccAuftragIDs = new ArrayList<>();
        ccAuftragIDs.add(ccAuftragId);
        return findDomains4Auftraege(ccAuftragIDs);
    }

    @Override
    public List<IntDomain> findDomains4Auftraege(List<Long> ccAuftragIDs) throws FindException {
        if (ccAuftragIDs == null) { return null; }
        try {
            List<Long> auftragNoOrigs = new ArrayList<>();
            for (Long ccAuftragId : ccAuftragIDs) {
                AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(ccAuftragId);
                if (auftragDaten != null) {
                    Long auftragNoOrig = auftragDaten.getAuftragNoOrig();
                    auftragNoOrigs.add(auftragNoOrig);
                }
            }

            return domainDao.findByAuftragNoOrigs(auftragNoOrigs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

}


