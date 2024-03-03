/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.01.2015
 */
package de.augustakom.common.tools.dao.hibernate;

import java.io.*;

/**
 * Created by maherma on 28.01.2015.
 */
public class Hibernate4DefaultDeleteDAO extends Hibernate4DefaultDAO {

    /**
     * Loescht das übergebene Objekt aus der Datenbank.
     *
     * @param toDelete zu löschendes Objekts.
     */
    public void delete(Object toDelete) {
        getSession().delete(toDelete);
    }

    /**
     * Loescht das durch die ID referenzierte Objekt aus der Datenbank.
     *
     * @param id   die ID
     * @param type der Entity Typ
     */
    public void deleteById(Serializable id, Class<?> type) {
        Object entity = findById(id, type);
        delete(entity);
    }

}
