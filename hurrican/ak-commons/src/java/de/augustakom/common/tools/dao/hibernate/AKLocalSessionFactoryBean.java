/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2005 11:56:38
 */
package de.augustakom.common.tools.dao.hibernate;

import java.util.*;
import java.util.Map.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;


/**
 * Sub-Klasse von <code>LocalSessionFactoryBean</code>. Diese Klasse ueberprueft, ob fuer ein Hibernate-Property (z.B.
 * 'hibernate.default_schema') ein leerer String (oder null) angegeben wurde. Ist dies der Fall, wird das Property
 * entfernt.
 *
 *
 */
public class AKLocalSessionFactoryBean extends LocalSessionFactoryBean {

    /**
     * @see org.springframework.orm.hibernate4.LocalSessionFactoryBean#setHibernateProperties(java.util.Properties)
     * Sollte ein Hibernate-Property (z.B. 'hibernate.default_schema') ein Leerstring oder ein null-Wert sein, wird das
     * Property heraus gefiltert.
     */
    @Override
    public void setHibernateProperties(Properties hibernateProperties) {
        if (hibernateProperties != null) {
            List<String> toRemove = new ArrayList<String>();

            for (Entry<Object, Object> prop : hibernateProperties.entrySet()) {
                String value = (String) prop.getValue();
                if (StringUtils.isBlank(value)) {
                    toRemove.add((String) prop.getKey());
                }
            }

            for (Iterator<String> iter = toRemove.iterator(); iter.hasNext(); ) {
                String key2Remove = iter.next();
                hibernateProperties.remove(key2Remove);
            }

            super.setHibernateProperties(hibernateProperties);
        }
    }

}


