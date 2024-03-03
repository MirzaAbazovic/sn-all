/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.2004 15:27:40
 */
package de.augustakom.common.tools.dao.iface;

import java.util.*;
import org.hibernate.criterion.Criterion;


/**
 * DAO-Interface, um ueber ein sog. Example-Objekt zu filtern. <br> Eine moegliche (Hibernate)Implementierung koennte
 * wie folgt aussehen: <br> <code> Session session = getSession(); <br> try { <br> Criteria criteria =
 * session.createCriteria(type); <br> criteria.add(Example.create(example)); <br> return criteria.list(); <br> } <br>
 * catch (HibernateException e) { <br> throw SessionFactoryUtils.convertHibernateAccessException(e); <br> } <br> finally
 * { <br> SessionFactoryUtils.closeSessionIfNecessary(session, getSessionFactory()); <br> } <br> </code>
 *
 *
 */
public interface ByExampleDAO {

    /**
     * Filtert ueber ein sog. Example-Objekt.
     *
     * @param example Example-Objekt, das die gesuchten Parameter enthaelt.
     * @param type    Typ der gesuchten Objekte.
     * @return Liste mit Objekten des Typs <code>type</code>
     */
    <T> List<T> queryByExample(Object example, Class<T> type);

    /**
     * @param example
     * @param type
     * @param enableLike
     * @return
     * @see ByExampleDAO#queryByExample(Object, Class) Durch das Boolean-Flag kann definiert werden, ob die Suche auch
     * per Wildcards erlaubt ist.
     */
    <T> List<T> queryByExample(Object example, Class<T> type, boolean enableLike);

    /**
     * @param example         Example-Objekt, das die gesuchten Parameter enthaelt.
     * @param type            Typ der gesuchten Objekte.
     * @param orderParamsAsc  Liste mit Parameternamen, nach denen aufsteigend sortiert werden soll
     * @param orderParamsDesc Liste mit Parameternamen, nach denen absteigend sortiert werden soll
     * @return Liste mit Objekten des Typs <code>type</code>
     *
     * @see ByExampleDAO#queryByExample(Object, Class) Zusaetzlich bietet diese Methode die Moeglichkeit, das Ergebnis sortiert zu
     * erhalten.
     */
    <T> List<T> queryByExample(Object example, Class<T> type, String[] orderParamsAsc, String[] orderParamsDesc);

    /**
     * Suche ueber ein bereits erstelltes Example Kriterium.
     * @param example       Example-Objekt, das die gesuchten Parameter enthaelt.
     * @param type          Typ der gesuchten Objekte.
     * @param <T>
     * @param <V>
     * @return
     */
    <T, V extends T> List<T> queryByCreatedExample(final Criterion example, final Class<V> type);
}


