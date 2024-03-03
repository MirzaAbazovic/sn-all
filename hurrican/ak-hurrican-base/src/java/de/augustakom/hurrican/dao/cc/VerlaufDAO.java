/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 16:08:57
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.view.SimpleVerlaufView;

/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>Verlauf</code>.
 *
 *
 */
public interface VerlaufDAO extends StoreDAO, FindDAO, FindAllDAO, ByExampleDAO {

    /**
     * Ueberprueft, ob zu dem Auftrag mit der ID <code>auftragId</code> ein aktiver Verlauf vorhanden ist.
     *
     * @param auftragId ID des zu ueberpruefenden Auftrags
     * @return true, wenn ein aktiver Verlauf zu dem Auftrag existiert.
     */
    boolean hasActiveVerlauf(Long auftragId);

    /**
     * Sucht nach allen aktiven Verlaeufen fuer einen best. Auftrag.
     *
     * @param auftragId     ID des Auftrags zu dem die aktiven Verlaeufe gesucht werden.
     * @param projektierung Flag, ob nach Projektierungen (true) oder Bauauftraegen (false) gesucht werden soll.
     * @return Liste mit Objekten des Typs <code>Verlauf</code>.
     */
    List<Verlauf> findActive(Long auftragId, boolean projektierung);

    /**
     * Sucht nach dem <b>letzten</b> Verlauf fuer einen best. Auftrag. <br> Ueber das Flag <code>projektierung</code>
     * wird angegeben, ob nach der letzten Projektierung (true) oder dem letzten Bauauftrag (false) fuer den Auftrag
     * gesucht werden soll.
     *
     * @param auftragId     Auftrags-ID
     * @param projektierung Flag, ob nach der letzten Projektierung oder dem letzten Bauauftrag gesucht werden soll.
     * @return Instanz von <code>Verlauf</code>.
     */
    Verlauf findLast4Auftrag(Long auftragId, boolean projektierung);

    /**
     * Sucht nach allen Verlaeufen eines best. Auftrags. <br> Die Verlaeufe werden absteigend (nach der ID) sortiert.
     *
     * @param auftragId ID des Auftrags, dessen Verlaeufe gesucht werden.
     * @return Liste mit Objekten des Typs <code>Verlauf</code> oder </code>null</code>.
     */
    List<Verlauf> findByAuftragId(Long auftragId);

    /**
     * Sucht den Verlaeuf zu einer best. Workforce Order. <br> Die Verlaeufe werden absteigend (nach der ID) sortiert.
     *
     * @param workforceOrderId WorkForceOrder ID des Auftrags, dessen Verlaeufe gesucht werden.
     * @return Liste mit Objekten des Typs <code>Verlauf</code> oder </code>null</code>.
     */
    List<Verlauf> findByWorkforceOrderId(String workforceOrderId);

    /**
     * Sucht nach den wichtigsten Verlaufs-Daten zu einem best. Auftrag. <br> Die Sortierung der Daten erfolgt
     * absteigend!
     *
     * @param auftragId ID des Auftrags, dessen Verlaufs-Daten gesucht werden.
     * @return Liste mit Objekten des Typs <code>SimpleVerlaufView</code>.
     */
    List<SimpleVerlaufView> findSimpleVerlaufViews(Long auftragId);

    /**
     * Liefert alle Auftragsnummern, die seit einem bestimmten Datum in Betrieb gingen. Das Datum wird geprueft anhand
     * des Felds ausgetragen_am der Tabelle Verlauf_Abteilung
     *
     * @param date Datum
     * @return Liste mit Auftragsnummern
     *
     */
    List<Long> findFinishedOrderByDate(Date date);

    /**
     * Liefert alle abgeschlossenen Verlaeufe seit einem bestimmten Datum. Das Datum wird geprueft anhand des Felds
     * ausgetragen_am der Tabelle Verlauf_Abteilung
     *
     * @param date Datum
     * @return Liste mit Verluf-Objekten
     *
     */
    List<Verlauf> findFinishedVerlaufByDate(Date date);

}
