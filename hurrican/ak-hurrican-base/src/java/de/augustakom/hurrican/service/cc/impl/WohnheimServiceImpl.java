/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 09:24:25
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Wohnheim;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.WohnheimService;


/**
 * Service-Implementierung von <code>WohnheimService</code>.
 *
 *
 */
@CcTxRequired
public class WohnheimServiceImpl extends DefaultCCService implements WohnheimService {

    private static final Logger LOGGER = Logger.getLogger(WohnheimServiceImpl.class);

    /**
     * @see de.augustakom.hurrican.service.cc.WohnheimService#findByKundeNoOrig(Long)
     */
    public List<Wohnheim> findByKundeNoOrig(Long kNoOrig) throws FindException {
        try {
            ByExampleDAO dao = (ByExampleDAO) getDAO();
            Wohnheim example = new Wohnheim();
            example.setKundeNo(kNoOrig);
            return dao.queryByExample(example, Wohnheim.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.WohnheimService#findByVbz(java.lang.String)
     */
    public Wohnheim findByVbz(String vbz) throws FindException {
        try {
            ByExampleDAO dao = (ByExampleDAO) getDAO();
            Wohnheim example = new Wohnheim();
            example.setVbz(vbz);
            List result = dao.queryByExample(example, Wohnheim.class);
            return (result != null && result.size() == 1) ? (Wohnheim) result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


}


