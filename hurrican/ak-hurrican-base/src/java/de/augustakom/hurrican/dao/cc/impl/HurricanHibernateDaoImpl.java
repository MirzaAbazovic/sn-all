/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.04.2012 09:39:54
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Session;
import org.springframework.dao.OptimisticLockingFailureException;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;
import de.augustakom.hurrican.service.utils.HistoryHelper;

public abstract class HurricanHibernateDaoImpl extends Hibernate4DAOImpl {

    protected <T extends AbstractCCHistoryModel> T update4History(final T objectToHistorize, final T emptyNewObject,
            final Serializable id, final Date gueltigBis) {

        Session session = getSessionFactory().getCurrentSession();
        session.evict(objectToHistorize);

        try {
            AbstractCCHistoryModel oldVersion = findById(id, objectToHistorize.getClass());
            if (oldVersion.getVersion().equals(objectToHistorize.getVersion())) {
                oldVersion.setGueltigBis(gueltigBis);
                PropertyUtils.copyProperties(emptyNewObject, objectToHistorize);
                HistoryHelper.setHistoryData(emptyNewObject, gueltigBis);
                emptyNewObject.setVersion(0L);
                store(oldVersion);
                store(emptyNewObject);
                return emptyNewObject;
            }
            else {
                throw new OptimisticLockingFailureException(
                        "Die zu historisierenden Daten wurden zwischenzeitlich verändert und sind nicht mehr aktuell! Bitte aktualisieren Sie und versuchen es erneut!");
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


