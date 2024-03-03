/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2011 14:24:57
 */
package de.mnet.wita.dao.impl;

import java.util.*;
import com.google.common.collect.Iterables;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.dao.AnbieterwechselConfigDao;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.AnbieterwechselConfig;
import de.mnet.wita.model.AnbieterwechselConfig.NeuProdukt;

public class AnbieterwechselConfigDaoImpl extends Hibernate4DAOImpl implements AnbieterwechselConfigDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AnbieterwechselConfig findConfig(Carrier carrierAbgebend, ProduktGruppe produktGruppe, NeuProdukt neuProdukt) {
        AnbieterwechselConfig example = new AnbieterwechselConfig();
        example.setCarrierAbgebend(carrierAbgebend);
        example.setAltProdukt(produktGruppe);
        example.setNeuProdukt(neuProdukt);

        List<AnbieterwechselConfig> configs = queryByExample(example, AnbieterwechselConfig.class);
        return Iterables.getOnlyElement(configs, null);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
