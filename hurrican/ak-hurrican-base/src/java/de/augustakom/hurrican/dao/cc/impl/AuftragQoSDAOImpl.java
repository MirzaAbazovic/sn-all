/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2008 13:55:55
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.AuftragQoSDAO;
import de.augustakom.hurrican.model.cc.AuftragQoS;


/**
 * Hibernate DAO-Implementierung von <code>AuftragQoSDAO</code>.
 *
 *
 */
public class AuftragQoSDAOImpl extends HurricanHibernateDaoImpl implements AuftragQoSDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.common.tools.dao.iface.ByExampleDAO#queryByExample(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type) {
        return getByExampleDAO().queryByExample(example, type, new String[] { "qosClassRefId", "id" }, null);
    }

    @Override
    public AuftragQoS update4History(AuftragQoS obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new AuftragQoS(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


