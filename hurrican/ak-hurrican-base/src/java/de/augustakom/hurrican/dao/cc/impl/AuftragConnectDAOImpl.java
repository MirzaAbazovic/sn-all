/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:56:23
 */

package de.augustakom.hurrican.dao.cc.impl;

import java.sql.Date;
import java.util.*;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.AuftragConnectDAO;
import de.augustakom.hurrican.model.cc.AuftragConnect;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;


public class AuftragConnectDAOImpl extends Hibernate4DAOImpl implements AuftragConnectDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AuftragConnect findAuftragConnectByAuftrag(CCAuftragModel auftrag) throws FindException {
        StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AuftragConnect.class.getName());
        hql.append(" ac where ac.auftragId = ? and ac.gueltigVon <= ? and ac.gueltigBis > ?");

        Date now = DateTools.getActualSQLDate();

        List<?> results = find(hql.toString(), new Object[] { auftrag.getAuftragId(), now, now });

        if (results != null) {
            if (results.size() == 1) {
                return (AuftragConnect) results.get(0);
            }
            else if (results.size() > 1) {
                throw new FindException("Mehr als einen Connect-Auftrag zu diesem Auftrag gefunden.");
            }
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
