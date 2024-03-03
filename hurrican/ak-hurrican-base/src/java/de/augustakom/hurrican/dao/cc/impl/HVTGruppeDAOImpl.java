/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 13:20:03
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.HVTGruppeDAO;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;


/**
 * Hibernate DAO-Implementierung fuer Objekte des Typs HVTGruppe.
 *
 *
 */
public class HVTGruppeDAOImpl extends Hibernate4DAOImpl implements HVTGruppeDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public HVTGruppe findHVTGruppe4Standort(Long standortId) {
        StringBuilder hql = new StringBuilder();
        hql.append("select g from ");
        hql.append(HVTGruppe.class.getName());
        hql.append(" g, ");
        hql.append(HVTStandort.class.getName());
        hql.append(" s where s.hvtGruppeId=g.id and s.id=?");

        @SuppressWarnings("unchecked")
        List<HVTGruppe> result = find(hql.toString(), standortId);
        if ((result != null) && (result.size() == 1)) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public HVTGruppe findHVTGruppeById(Long gruppenId) {
        return findById(gruppenId, HVTGruppe.class);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


