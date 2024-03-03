/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 08:16:19
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Abstrakte Klasse fuer alle Commands, die Physik-Daten von Auftraegen aendern.
 *
 *
 */
public abstract class AbstractPhysikCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractPhysikCommand.class);

    @Autowired
    @Qualifier("cc.sessionFactory")
    private SessionFactory sessionFactory;

    /**
     * Key fuer die prepare-Methode, um die Session-ID des Users zu uebergeben.
     */
    public static final String KEY_SESSION_ID = "session.id";
    /**
     * Key fuer die prepare-Methode, um die ID des Ursprungs-Auftrags fuer die Physik-Aktion zu uebergeben.
     */
    public static final String KEY_AUFTRAG_ID_SRC = "auftrag.id.src";
    /**
     * Key fuer die prepare-Methode, um die ID des neuen Auftrags fuer die Physik-Aktion zu uebergeben.
     */
    public static final String KEY_AUFTRAG_ID_DEST = "auftrag.id.dest";
    /**
     * Key fuer die prepare-Methode, um die Physikaenderungs-Strategie zu uebergeben.
     */
    public static final String KEY_STRATEGY = "physik.strategy";
    /**
     * Key fuer die prepare-Methode, um das ServiceCallback-Objekt zu uebergeben.
     */
    public static final String KEY_SERVICE_CALLBACK = "service.callback";

    /**
     * Gibt die ID des Ursprungs-Auftrags zurueck.
     */
    protected Long getAuftragIdSrc() {
        return (Long) getPreparedValue(KEY_AUFTRAG_ID_SRC);
    }

    /**
     * Gibt die ID des 'neuen' Auftrags zurueck.
     */
    protected Long getAuftragIdDest() {
        return (Long) getPreparedValue(KEY_AUFTRAG_ID_DEST);
    }

    /**
     * Gibt die Session-ID des Users zurueck, der das Command ausgeloest hat.
     */
    protected Long getSessionId() {
        return (Long) getPreparedValue(KEY_SESSION_ID);
    }

    /**
     * Gibt die Uebernahme-Strategie zurueck.
     */
    protected long getStrategy() {
        Object tmp = getPreparedValue(KEY_STRATEGY);
        return (tmp instanceof Number) ? ((Number) tmp).longValue() : -1;
    }

    @Override
    public final Object execute() throws Exception {
        flushHibernateSession();
        return executeAfterFlush();
    }

    protected abstract Object executeAfterFlush() throws Exception;

    protected void flushHibernateSession() {
        if (sessionFactory != null) {
            Session session = sessionFactory.getCurrentSession();
            if (session != null) {
                LOGGER.info("Flushing Hibernate-Session from Advice...");
                session.flush();
            }
        }
    }
}


