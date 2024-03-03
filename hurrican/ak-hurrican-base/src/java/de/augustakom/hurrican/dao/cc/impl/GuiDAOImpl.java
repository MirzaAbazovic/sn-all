/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 10:25:46
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.GuiDAO;
import de.augustakom.hurrican.model.cc.gui.GUIDefinition;
import de.augustakom.hurrican.model.cc.gui.GUIMapping;


/**
 * Hibernate DAO-Implementierung, um GUI-Objekte (GUIDefinition, GUIMapping) zu verwalten.
 *
 *
 */
public class GuiDAOImpl implements GuiDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public GUIDefinition findGUIDefByClass(String className) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(GUIDefinition.class.getName());
        hql.append(" g where g.clazz=:className and g.active=:active");

        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setString("className", className);
        query.setBoolean("active", Boolean.TRUE);

        @SuppressWarnings("unchecked")
        List<GUIDefinition> result = (List<GUIDefinition>) query.list();
        return ((result != null) && (result.size() == 1)) ? (GUIDefinition) result.get(0) : null;
    }

    @Override
    public List<GUIDefinition> findGUIDefinitionen(Long referenceId, String refHerkunft, String guiType) {
        StringBuilder hql = new StringBuilder();
        hql.append("select d.id, d.clazz, d.type, d.name, d.text, d.tooltip, d.icon, d.addSeparator, d.orderNo from ");
        hql.append(GUIDefinition.class.getName()).append(" d, ");
        hql.append(GUIMapping.class.getName()).append(" m ");
        hql.append(" where (m.referenzId=:referenceId or m.referenzId=:referenceIdAll) ");
        hql.append(" and m.referenzHerkunft=:refHerkunft ");
        hql.append(" and d.type=:guiType and m.guiDefinitionId=d.id and d.active=:active ");
        hql.append(" order by d.orderNo");

        // --
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setLong("referenceId", referenceId);
        query.setLong("referenceIdAll", GUIMapping.REFERENZ_ID_ALL);
        query.setString("refHerkunft", refHerkunft);
        query.setString("guiType", guiType);
        query.setBoolean("active", Boolean.TRUE);

        @SuppressWarnings("unchecked")
        List<Object[]> result = (List<Object[]>) query.list();
        if (result != null) {
            List<GUIDefinition> retVal = new ArrayList<GUIDefinition>();
            for (int i = 0; i < result.size(); i++) {
                Object[] values = result.get(i);

                GUIDefinition gd = new GUIDefinition();
                gd.setId(ObjectTools.getLongSilent(values, 0));
                gd.setClazz(ObjectTools.getStringSilent(values, 1));
                gd.setType(ObjectTools.getStringSilent(values, 2));
                gd.setName(ObjectTools.getStringSilent(values, 3));
                gd.setText(ObjectTools.getStringSilent(values, 4));
                gd.setTooltip(ObjectTools.getStringSilent(values, 5));
                gd.setIcon(ObjectTools.getStringSilent(values, 6));
                gd.setAddSeparator(ObjectTools.getBooleanSilent(values, 7));
                gd.setOrderNo(ObjectTools.getIntegerSilent(values, 8));

                retVal.add(gd);
            }

            return retVal;
        }

        return null;
    }

}


