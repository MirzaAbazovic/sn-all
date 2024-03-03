/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 15:33:10
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DefaultDAO;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragConnectDAO;
import de.augustakom.hurrican.model.cc.AuftragConnect;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.ConnectService;

@CcTxRequired
public class ConnectServiceImpl extends DefaultCCService implements ConnectService {

    private static final Logger LOGGER = Logger.getLogger(ConnectServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.dao.cc.AuftragConnectDAO")
    private AuftragConnectDAO auftragConnectDAO;
    @Resource(name = "defaultDAO")
    private Hibernate4DefaultDAO hibernateDefaultDAO;

    @Override
    public void saveAuftragConnect(AuftragConnect toSave) throws StoreException {
        Assert.notNull(toSave);
        try {
            auftragConnectDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragConnect findAuftragConnectByAuftrag(CCAuftragModel auftrag) throws FindException {
        Assert.notNull(auftrag);
        try {
            return auftragConnectDAO.findAuftragConnectByAuftrag(auftrag);
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveEndstelleConnect(EndstelleConnect toSave) throws StoreException {
        Assert.notNull(toSave);
        try {
            hibernateDefaultDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public EndstelleConnect findEndstelleConnectByEndstelle(Endstelle endstelle) throws FindException {
        Assert.notNull(endstelle);
        try {
            EndstelleConnect example = new EndstelleConnect();
            example.setEndstelleId(endstelle.getId());

            List<EndstelleConnect> results = hibernateDefaultDAO.queryByExample(example, EndstelleConnect.class);
            if (results != null) {
                if (results.size() == 1) {
                    return results.get(0);
                }
                else if (results.size() > 1) {
                    throw new FindException("Mehr als einen Endstelle-Connect-Eintrag zu dieser Endstelle gefunden.");
                }
            }
            return null;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }
}
