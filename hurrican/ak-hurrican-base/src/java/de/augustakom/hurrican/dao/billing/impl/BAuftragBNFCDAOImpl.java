/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2006 14:37:20
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.billing.BAuftragBNFCDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBNFC;
import de.augustakom.hurrican.model.billing.query.BAuftragBNFCQuery;
import de.augustakom.hurrican.model.shared.view.AuftragINRufnummerView;


/**
 * Hibernate DAO-Implementierung von <code>BAuftragBNFCDAO</code>
 *
 *
 */
public class BAuftragBNFCDAOImpl extends Hibernate4DAOImpl implements BAuftragBNFCDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.billing.BAuftragBNFCDAO#findINViews(de.augustakom.hurrican.model.billing.query.BAuftragBNFCQuery)
     */
    public List<AuftragINRufnummerView> findINViews(BAuftragBNFCQuery query) {
        List<Object> params = new ArrayList<Object>();
        params.add(Boolean.TRUE);

        StringBuilder hql = new StringBuilder("select a.auftragNoOrig, a.kundeNo, bnfc.prefix, ");
        hql.append("bnfc.businessNr from ");
        hql.append(BAuftrag.class.getName()).append(" a, ");
        hql.append(BAuftragBNFC.class.getName()).append(" bnfc where bnfc.auftragNo=a.auftragNo and a.histLast=? ");
        if (StringUtils.isNotBlank(query.getPrefix())) {
            if (WildcardTools.containsWildcard(query.getPrefix())) {
                hql.append("and bnfc.prefix like ? ");
                params.add(WildcardTools.replaceWildcards(query.getPrefix()));
            }
            else {
                hql.append("and bnfc.prefix=? ");
                params.add(query.getPrefix());
            }
        }
        if (StringUtils.isNotBlank(query.getBusinessNr())) {
            if (WildcardTools.containsWildcard(query.getBusinessNr())) {
                hql.append("and bnfc.businessNr like ? ");
                params.add(WildcardTools.replaceWildcards(query.getBusinessNr()));
            }
            else {
                hql.append("and bnfc.businessNr=? ");
                params.add(query.getBusinessNr());
            }
        }

        List<Object[]> result = find(hql.toString(), params.toArray());
        if (result != null) {
            List<AuftragINRufnummerView> retVal = new ArrayList<AuftragINRufnummerView>();
            for (Object[] values : result) {
                AuftragINRufnummerView view = new AuftragINRufnummerView();
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, 0));
                view.setKundeNo(ObjectTools.getLongSilent(values, 1));
                view.setPrefix(ObjectTools.getStringSilent(values, 2));
                view.setBusinessNr(ObjectTools.getStringSilent(values, 3));
                retVal.add(view);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


