/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2008 13:20:02
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.billing.BillDAO;
import de.augustakom.hurrican.model.billing.view.BillRunView;


/**
 * Hibernate DAO-Implementierung von <code>BillDAO</code>
 *
 *
 */
public class BillDAOImpl implements BillDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.billing.BillDAO#findBillRunViews()
     */
    public List<BillRunView> findBillRunViews() {
        final String sql = "SELECT "
                + "  r.RUN_NO AS runNo, "
                + "  r.PERIOD AS period, "
                + "  r.STATE AS status, "
                + "  r.INVOICE_DATE AS invoiceDate, "
                + "  c.SHORTDESC AS billCycle "
                + "FROM BIE_RUN r, "
                + "BIE_CYCLE c "
                + "WHERE "
                + "  r.CYCLE_NO = c.CYCLE_NO "
                + "ORDER BY "
                + " r.RUN_NO DESC";
        @SuppressWarnings("unchecked")
        List<BillRunView> results = sessionFactory.getCurrentSession()
                .createSQLQuery(sql)
                .addScalar("runNo", StandardBasicTypes.LONG)
                .addScalar("period", StandardBasicTypes.LONG)
                .addScalar("status")
                .addScalar("invoiceDate", StandardBasicTypes.DATE)
                .addScalar("billCycle")
                .setResultTransformer(Transformers.aliasToBean(BillRunView.class)).list();

        return results;
    }
}
