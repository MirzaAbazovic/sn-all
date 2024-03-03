/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2005 16:40:36
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.CCAuftragViewDAO;
import de.augustakom.hurrican.dao.cc.DBQueryDefDAO;
import de.augustakom.hurrican.dao.cc.ScvViewDAO;
import de.augustakom.hurrican.model.cc.DBQueryDef;
import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.ScvViewService;


/**
 * Service-Implementierung von <code>ScvViewService</code>. <br> <br> Besonderheit: <br> Der Service veranlasst ein
 * DAO-Objekt dazu, eine eigene (embedded) Datenbank (HSQLDB) anzulegen. In dieser Datenbank werden die Daten fuer die
 * verschiedenen Abfragen gespeichert. Beim Beenden des Services wird die embedden-DB (ueber das DAO-Objekt)
 * heruntergefahren.
 *
 *
 */
public class ScvViewServiceImpl extends DefaultCCService implements ScvViewService, DisposableBean {

    private static final Logger LOGGER = Logger.getLogger(ScvViewServiceImpl.class);

    private CCAuftragViewDAO auftragViewDAO = null;
    private DBQueryDefDAO dbQueryDefDAO = null;

    private boolean initialized = false;
    private boolean serviceDestroyed = false;

    private Date gueltigVon;


    /* Initialisiert den Service. */
    private void initialize(final Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        if (!initialized) {
            initialized = true;
            serviceDestroyed = false;
            ((ScvViewDAO) getDAO()).initializeDB();
            fetchOffeneAuftraege(gueltigVon);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.ScvViewService#reInitialize()
     */
    public void reInitialize() {
        try {
            initialized = false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public synchronized List<IncompleteAuftragView> findByParam(short strategy, Object[] params, final Date gueltigVon) throws FindException {
        if (!DateTools.isDateEqual(gueltigVon, this.gueltigVon)) {
            this.gueltigVon = gueltigVon;
            this.initialized = false;
        }
        return findByParam(strategy, params);
    }
    /**
     * @see de.augustakom.hurrican.service.base.iface.FindByParamService#findByParam(short, java.lang.Object[])
     */
    @Override
    @CcTxRequired
    public synchronized List<IncompleteAuftragView> findByParam(short strategy, Object[] params) throws FindException {
        try {
            initialize(gueltigVon);

            switch (strategy) {
                case FIND_STRATEGY_ALL:
                    return ((ScvViewDAO) getDAO()).findAll();
                case FIND_STRATEGY_WITHOUT_BA:
                    return ((ScvViewDAO) getDAO()).findWithoutBA();
                case FIND_STRATEGY_WITHOUT_BA_UEBERFAELLIG:
                    return ((ScvViewDAO) getDAO()).findWithoutBAUeberfaellig();
                case FIND_STRATEGY_WITHOUT_LBZ:
                    return ((ScvViewDAO) getDAO()).findWithoutLbz();
                case FIND_STRATEGY_CUDA_BESTELLUNG_OFFEN:
                    return ((ScvViewDAO) getDAO()).findCuDaBestellungen();
                case FIND_STRATEGY_CUDA_KUENDIGUNG_OFFEN:
                    return ((ScvViewDAO) getDAO()).findCuDaKuendigungen();
                case FIND_STRATEGY_VORGABESCV_AND_REALDATE:
                    if (params != null && params.length == 2) {
                        Date vorgabeScv = (params[0] instanceof Date) ? (Date) params[0] : null;
                        Date realDate = (params[1] instanceof Date) ? (Date) params[1] : null;
                        return ((ScvViewDAO) getDAO()).findByDates(vorgabeScv, realDate);
                    }
                    else {
                        throw new FindException(FindException.INVALID_FIND_PARAMETER);
                    }
                default:
                    throw new FindException(FindException.FIND_STRATEGY_NOT_SUPPORTED);
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Wird - hoffentlich :~) - von Spring aufgerufen, wenn der Service beendet wird. <br>
     */
    public void destroyService() {
        if (!serviceDestroyed && initialized) {
            serviceDestroyed = true;
            try {
                ((ScvViewDAO) getDAO()).shutdownDB();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        destroyService();
    }

    /*
     * Sucht nach allen offenen Auftraegen und speichert Sie in
     * einer Zwischentabelle.
     */
    private void fetchOffeneAuftraege(final Date gueltigVon) {
        List<IncompleteAuftragView> views = getAuftragViewDAO().findIncomplete(gueltigVon);
        if (views != null && !views.isEmpty()) {
            try {
                KundenService ks = getBillingService(KundenService.class);
                ks.loadKundendaten4AuftragViews(views);
            }
            catch (Exception e) {
                LOGGER.error("ERROR while loading <Kundendaten> for IncompleteAuftragViews!", e);
            }

            ((ScvViewDAO) getDAO()).insert(views);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.ScvViewService#findDBQueryDefs()
     */
    @CcTxRequiredReadOnly
    public List<DBQueryDef> findDBQueryDefs() throws FindException {
        try {
            return getDbQueryDefDAO().findAll(DBQueryDef.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @return Returns the auftragViewDAO.
     */
    public CCAuftragViewDAO getAuftragViewDAO() {
        return auftragViewDAO;
    }

    /**
     * @param auftragViewDAO The auftragViewDAO to set.
     */
    public void setAuftragViewDAO(CCAuftragViewDAO auftragViewDAO) {
        this.auftragViewDAO = auftragViewDAO;
    }

    /**
     * @return Returns the dbQueryDefDAO.
     */
    public DBQueryDefDAO getDbQueryDefDAO() {
        return dbQueryDefDAO;
    }

    /**
     * @param dbQueryDefDAO The dbQueryDefDAO to set.
     */
    public void setDbQueryDefDAO(DBQueryDefDAO dbQueryDefDAO) {
        this.dbQueryDefDAO = dbQueryDefDAO;
    }

}


