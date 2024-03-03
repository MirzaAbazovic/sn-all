/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2005 08:00:51
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;
import org.springframework.dao.DataAccessResourceFailureException;

import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;


/**
 * DAO-Interface fuer die Ermittlung von speziellen SCV-Views.
 *
 *
 */
public interface ScvViewDAO {

    /**
     * Initialisiert eine Embedded-Datenbank, die fuer die Abfragen der SCV-Views verwendet wird. <br> In dieser Methode
     * wird die DB-Connection aufgebaut und die DB-Struktur angelegt. <br> Der Inhalt von vorhandenen Tabellen wird
     * ueber diese Methode komplett geloescht.
     *
     * @throws DataAccessResourceFailureException wenn keine Connection zur Embedded-DB hergestellt oder wenn die
     *                                            Struktur der DB nicht angelegt werden konnte.
     */
    public void initializeDB() throws DataAccessResourceFailureException;

    /**
     * Diese Methode sollte vor Programmende aufgerufen werden, um die Embedded-Datenbank zu schliessen.
     */
    public void shutdownDB();

    /**
     * Fuegt die angegebenen Objekte in die Embedded-Datenbank ein.
     *
     * @param toInsert
     */
    public void insert(List<IncompleteAuftragView> toInsert);

    /**
     * Sucht nach allen offenen Auftraegen.
     *
     * @return Liste mit Objekten des Typs <code>IncompleteAuftragView</code>.
     */
    public List<IncompleteAuftragView> findAll();

    /**
     * Sucht nach allen offenen Auftraegen ohne Bauauftrag.
     *
     * @return Liste mit Objekten des Typs <code>IncompleteAuftragView</code>.
     */
    public List<IncompleteAuftragView> findWithoutBA();

    /**
     * Sucht nach allen offenen Auftraegen ohne Bauauftrag, dessen Vorgabe-Termin bereits abgelaufen ist.
     *
     * @return Liste mit Objekten des Typs <code>IncompleteAuftragView</code>.
     */
    public List<IncompleteAuftragView> findWithoutBAUeberfaellig();

    /**
     * Sucht nach allen offenen Auftraegen mit Carrierbestellung, bei denen die LBZ und das Rueckmeldedatum noch nicht
     * eingetragen wurde
     *
     * @return Liste mit Objekten des Typs <code>IncompleteAuftragView</code>.
     */
    public List<IncompleteAuftragView> findWithoutLbz();

    /**
     * Sucht nach allen Auftraegen, bei denen die Rueckmeldung der CuDa-Bestellung bereits ueberfaellig ist.
     *
     * @return Liste mit Objekten des Typs <code>IncompleteAuftragView</code>.
     */
    public List<IncompleteAuftragView> findCuDaBestellungen();

    /**
     * Sucht nach allen Auftraegen, bei denen die Rueckmeldung der CuDa-Kuendigung bereits ueberfaellig ist.
     *
     * @return Liste mit Objekten des Typs <code>IncompleteAuftragView</code>.
     */
    public List<IncompleteAuftragView> findCuDaKuendigungen();

    /**
     * Sucht nach allen Auftraegen, bei denen das Vorgabe-SCV Datum und/oder der Realisierungstermin mit den
     * Suchparametern uebereinstimmen.
     *
     * @param vorgabeScv
     * @param realDate
     * @return Liste mit Objekten des Typs <code>IncompleteAuftragView</code>
     */
    public List<IncompleteAuftragView> findByDates(Date vorgabeScv, Date realDate);

}


