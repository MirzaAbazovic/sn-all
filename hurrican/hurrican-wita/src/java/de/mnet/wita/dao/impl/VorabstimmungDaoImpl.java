/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 13:19:43
 */
package de.mnet.wita.dao.impl;

import java.util.*;
import com.google.common.collect.Iterables;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.mnet.wita.dao.VorabstimmungDao;
import de.mnet.wita.model.Vorabstimmung;

public class VorabstimmungDaoImpl extends Hibernate4DAOImpl implements VorabstimmungDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public Vorabstimmung findVorabstimmung(String endstelleTyp, Long auftragId) {
        Vorabstimmung example = new Vorabstimmung();
        example.setEndstelleTyp(endstelleTyp);
        example.setAuftragId(auftragId);

        List<Vorabstimmung> configs = queryByExample(example, Vorabstimmung.class);

        // Guaranteed by database constraint
        return Iterables.getOnlyElement(configs, null);
    }

    @Override
    public List<Vorabstimmung> findVorabstimmungen(Long auftragId) {
        Vorabstimmung example = new Vorabstimmung();
        example.setAuftragId(auftragId);

        return queryByExample(example, Vorabstimmung.class);
    }

    @Override
    public void deleteVorabstimmung(Vorabstimmung vorabstimmungAufnehmend) {
        sessionFactory.getCurrentSession().delete(vorabstimmungAufnehmend);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
