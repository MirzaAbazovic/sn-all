/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2005 14:54:59
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.DialerDAO;
import de.augustakom.hurrican.dao.cc.PortGesamtDAO;
import de.augustakom.hurrican.model.cc.Dialer;
import de.augustakom.hurrican.model.cc.PortGesamt;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EWSDService;

/**
 * Service-Implementierung von <code>EWSDService</code>.
 *
 *
 */
@CcTxRequired
public class EWSDServiceImpl extends DefaultCCService implements EWSDService {

    private static final Logger LOGGER = Logger.getLogger(EWSDServiceImpl.class);

    @Resource(name = "dialerDAO")
    private DialerDAO dialerDAO = null;
    @Resource(name = "portGesamtDAO")
    private PortGesamtDAO portGesamtDAO;

    @Override
    public List<Dialer> findDialer() throws FindException {
        return dialerDAO.findAll(Dialer.class);
    }

    @Override
    public void saveDialer(Dialer toStore) throws StoreException {
        if (toStore == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            dialerDAO.store(toStore);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public void importEWSDFiles(List<String> importFiles) throws StoreException {
        BufferedReader f = null;
        String line = null;
        try {
            if (CollectionTools.isNotEmpty(importFiles)) {
                for (String file : importFiles) {
                    f = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringTools.CC_DEFAULT_CHARSET));
                    PortGesamtDAO dao = portGesamtDAO;
                    while ((line = f.readLine()) != null) {
                        String s = line.trim();
                        PortGesamt pg = PortGesamt.createPortGesamt(s);
                        dao.store(pg);
                    }
                }
            }
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim einlesen der Datei!", e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
        finally {
            if (f != null) {
                try {
                    f.close();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void deletePortGesamt() throws DeleteException {
        try {
            PortGesamtDAO dao = portGesamtDAO;
            dao.deletePortGesamt();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public Date selectPortGesamtDate() throws FindException {
        try {
            Date aktDatum = portGesamtDAO.selectPortGesamtDate();
            return aktDatum;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

}
