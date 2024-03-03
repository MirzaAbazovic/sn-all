/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2011 17:46:22
 */
package de.mnet.wita.dao.impl;

import static org.hibernate.criterion.Restrictions.*;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.dao.WitaCBVorgangDao;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;

public class WitaCBVorgangDaoImpl extends Hibernate4DAOImpl implements WitaCBVorgangDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @Override
    public List<CBVorgang> findCbVorgaengeByAuftragOrCBId(final Long auftragId, final Long cbId) {
        if ((cbId == null) && (auftragId == null)) {
            throw new IllegalArgumentException("Die carrierbestellungId und auftragId dürfen nicht null sein.");
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session
                .createSQLQuery("SELECT * from T_CB_VORGANG CBV "
                        + "WHERE ((CBV.CB_ID is null AND CBV.AUFTRAG_ID = " + auftragId.toString()
                        + ") " + generateCbClause(cbId) + ") "
                        + "AND (CBV.WITA_AENDERUNGSKZ_LAST != '"
                        + AenderungsKennzeichen.STORNO.toString()
                        + "' OR CBV.WITA_AENDERUNGSKZ_LAST is null) "
                        + " ORDER BY CBV.ID DESC");

        query.addEntity(CBVorgang.class);
        return query.list();
    }

    private String generateCbClause(Long cbId) {
        if (cbId == null) {
            return "";
        }
        return "OR (CBV.CB_ID = " + cbId.toString() + ")";
    }

    @Override
    public List<WitaCBVorgang> findWitaCBVorgaengeForAutomation(Long... orderType) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(WitaCBVorgang.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, WitaCBVorgang.STATUS, CBVorgang.STATUS_ANSWERED);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, WitaCBVorgang.RETURN_OK, Boolean.TRUE);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, WitaCBVorgang.AENDERUNGS_KENNZEICHEN, AenderungsKennzeichen.STANDARD);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, WitaCBVorgang.AUTOMATION, Boolean.TRUE);
        crit.add(in(WitaCBVorgang.TYP, orderType));

        List<WitaCBVorgang> cbVorgangs = crit.list();
        return cbVorgangs;
    }

    @Override
    public List<Long> findWitaCBVorgangIDsForKlammerId(Long klammerId) {
        return find("select cb.cbId from WitaCBVorgang cb where cb.auftragsKlammer = ?", klammerId);
    }

    @Override
    public WitaCBVorgang findWitaCBVorgangByRefId(Long cbVorgangRefId) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(WitaCBVorgang.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, WitaCBVorgang.CB_VORGANG_REF_ID, cbVorgangRefId);
        List<WitaCBVorgang> cbVorgangs = crit.list();
        if (CollectionUtils.isNotEmpty(cbVorgangs)) {
            return cbVorgangs.get(0);
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
