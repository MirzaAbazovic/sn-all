package de.augustakom.hurrican.dao.cc.errorlog;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.ErrorLogEntry;

/**
 * Implementation of {@see ErrorLogDAO}
 * 
 */
@CcTxRequired
public class ErrorLogDAOImpl extends Hibernate4DAOImpl implements ErrorLogDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public List<ErrorLogEntry> findByService(String service) {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria crit = session.createCriteria(ErrorLogEntry.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "service", service);

        final List<ErrorLogEntry> results = crit.list();
        return results;
    }

}
