/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2012 11:18:12
 */
package de.mnet.wita.dao.impl;

import java.util.*;
import com.google.common.collect.Iterables;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.mnet.wita.dao.VorabstimmungAbgebendDao;
import de.mnet.wita.model.VorabstimmungAbgebend;

public class VorabstimmungAbgebendDaoImpl extends Hibernate4DAOImpl implements VorabstimmungAbgebendDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public VorabstimmungAbgebend findVorabstimmung(String endstelleTyp, Long auftragId) {
        VorabstimmungAbgebend example = new VorabstimmungAbgebend();
        example.setEndstelleTyp(endstelleTyp);
        example.setAuftragId(auftragId);
        List<VorabstimmungAbgebend> configs = queryByExample(example, VorabstimmungAbgebend.class);
        // Guaranteed by database constraint
        return Iterables.getOnlyElement(configs, null);
    }

    @Override
    public List<VorabstimmungAbgebend> findVorabstimmungen(Long auftragId) {
        VorabstimmungAbgebend example = new VorabstimmungAbgebend();
        example.setAuftragId(auftragId);
        return queryByExample(example, VorabstimmungAbgebend.class);
    }

    @Override
    public void deleteVorabstimmungAbgebend(VorabstimmungAbgebend vorabstimmungAbgebend) {
        sessionFactory.getCurrentSession().delete(vorabstimmungAbgebend);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
