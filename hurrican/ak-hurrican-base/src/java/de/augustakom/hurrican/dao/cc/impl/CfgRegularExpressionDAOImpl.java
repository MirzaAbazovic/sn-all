package de.augustakom.hurrican.dao.cc.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.CfgRegularExpressionDAO;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.CfgRegularExpression.Info;


/**
 * This DAO needs an active Transaction and relies on a thread-bound session
 *
 *
 */
public class CfgRegularExpressionDAOImpl extends Hibernate4DAOImpl implements CfgRegularExpressionDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public CfgRegularExpression findRegularExpression(final Long refId, final String refName,
           final Class<?> refClass, final Info requestedInfo) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CfgRegularExpression.class);
        if (refId != null) {
            criteria.add(Restrictions.eq(CfgRegularExpression.REF_ID, refId));
        }
        if (refName != null) {
            criteria.add(Restrictions.eq(CfgRegularExpression.REF_NAME, refName));
        }
        criteria.add(Restrictions.eq(CfgRegularExpression.REF_CLASS, refClass.getName()));
        criteria.add(Restrictions.eq(CfgRegularExpression.REQUESTED_INFO, requestedInfo.name()));
        return (CfgRegularExpression) criteria.uniqueResult();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
