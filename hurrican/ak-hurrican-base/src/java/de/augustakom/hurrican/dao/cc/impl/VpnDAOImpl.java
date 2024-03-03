/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 16:03:00
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.VpnDAO;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;


/**
 * Hibernate DAO-Implementierung von <code>VpnDAO</code>.
 *
 *
 */
public class VpnDAOImpl extends HurricanHibernateDaoImpl implements VpnDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public VPN findVPNByAuftragId(final Long ccAuftragId) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select v.id from ");
        hql.append(VPN.class.getName()).append(" v, ");
        hql.append(AuftragTechnik.class.getName()).append(" at ");
        hql.append(" where at.auftragId= :aId and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append(" and at.vpnId=v.id");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", ccAuftragId);
        q.setDate("now", new Date());

        List<Long> result = q.list();
        if ((result != null) && (result.size() == 1)) {
            Long vpnId = result.get(0);
            return findById(vpnId, VPN.class);
        }

        return null;
    }

    @Override
    public VPNKonfiguration findVPNKonfiguration4Auftrag(final Long ccAuftragId) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(VPNKonfiguration.class.getName());
        hql.append(" vpn where vpn.auftragId= :aId and vpn.gueltigVon<= :now and vpn.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", ccAuftragId);
        q.setDate("now", new Date());

        List result = q.list();
        return ((result != null) && (!result.isEmpty())) ? (VPNKonfiguration) result.get(0) : null;
    }

    @Override
    public VPNKonfiguration update4History(VPNKonfiguration obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new VPNKonfiguration(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


