/**
 * Copyright (c) 2010 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2010 16:25:55
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.MailDAO;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MailService;

public class MailDAOImpl extends Hibernate4DAOImpl implements MailDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @Override
    public List<Mail> findAllPendingMails() throws FindException {
        Session session = getSessionFactory().getCurrentSession();
        return session.createCriteria(Mail.class)
                .add(Restrictions.and(Restrictions.isNull("sentAt"),
                        Restrictions.lt("numberOfTries", MailService.MAX_NUMBER_OF_TRIES)))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
