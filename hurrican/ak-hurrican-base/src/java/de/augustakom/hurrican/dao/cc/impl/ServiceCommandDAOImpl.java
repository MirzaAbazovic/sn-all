/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 08:50:13
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.ServiceCommandDAO;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.command.ServiceCommandMapping;


/**
 * Hibernate DAO-Implementierung von <code>ServiceCommandDAO</code>.
 *
 *
 */
public class ServiceCommandDAOImpl extends Hibernate4DAOImpl implements ServiceCommandDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<ServiceCommand> findCommands4Reference(Long refId, Class refClass, String commandType) {
        List<Object> params = new ArrayList<Object>();
        params.add(refId);
        params.add(refClass.getName());

        StringBuilder hql = new StringBuilder("select cmd.id, cmd.name, cmd.className, cmd.type, cmd.description from ");
        hql.append(ServiceCommandMapping.class.getName()).append(" scm, ");
        hql.append(ServiceCommand.class.getName()).append(" cmd ");
        hql.append("where scm.commandId=cmd.id and scm.refId=? and scm.refClass=? ");
        if (StringUtils.isNotBlank(commandType)) {
            hql.append("and cmd.type=? ");
            params.add(commandType);
        }
        hql.append("order by scm.orderNo asc");

        @SuppressWarnings("unchecked")
        List<Object[]> result = find(hql.toString(), params.toArray());
        if (result != null) {
            List<ServiceCommand> retVal = new ArrayList<ServiceCommand>();
            for (Object[] values : result) {
                ServiceCommand sc = new ServiceCommand();
                sc.setId(ObjectTools.getLongSilent(values, 0));
                sc.setName(ObjectTools.getStringSilent(values, 1));
                sc.setClassName(ObjectTools.getStringSilent(values, 2));
                sc.setType(ObjectTools.getStringSilent(values, 3));
                sc.setDescription(ObjectTools.getStringSilent(values, 4));
                retVal.add(sc);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public int deleteCommands4Reference(final Long refId, final Class refClass) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(ServiceCommandMapping.class.getName());
        hql.append(" scm where scm.refId=? and scm.refClass=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameters(new Object[] { refId, refClass.getName() }, new Type[] { new LongType(), new StringType()});
        return query.executeUpdate();
    }

    @Override
    public void deleteServiceChain(final Long chainId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(ServiceChain.class.getName());
        hql.append(" sc where sc.id=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, chainId);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


