/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2004 08:15:37
 */
package de.augustakom.hurrican.dao.cc;

import java.time.*;
import java.util.*;

import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;

/**
 * DAO-Interface fuer Objekte des Typs <code>de.augustakom.hurrican.model.cc.Auftrag</code>
 *
 *
 */
public interface CCAuftragDAO extends FindDAO, FindAllDAO, StoreDAO {

    /**
     * Speichert das Auftrags-Objekt <code>toSave</code>. <br> Wichtig: Die Methode fuehrt nur Insert-Operationen durch,
     * keine Updates !!!
     *
     * @param toSave zu speicherndes Auftrag-Objekt.
     */
    void save(Auftrag toSave);

    /**
     * Ermittelt alle (CC-)Auftraege, die in das Kundenportal MNet exportiert werden sollen und im angegebenen Zeitraum
     * eine Realisierung haben.
     *
     * @param minRealDate (optional) minimales Realisierungsdatum - wenn nicht gesetzt, werden alle Verlaeufe der
     *                    Vergangenheit beruecksichtigt.
     * @param maxRealDate maximales Realisierungsdatum, bis zu dem die Verlaeufe beruecksichtigt werden sollen.
     * @return
     */
    List<Auftrag> findAuftraege4Export(Date minRealDate, Date maxRealDate);

    /**
     * Ermittelt die hoechste Auftrags-Nummer aus dem CC-System.
     *
     * @return
     */
    Long getMaxAuftragId();

    /**
     * Ermittelt alle Hurrican Auftraege zu einer LineId (=VBZ), die den Status 'in Betrieb' haben und alle gekuendigten
     * Auftraege deren Kuendigung vor {@code when} ist.
     *
     * @param lineId
     * @param when
     * @return
     */
    List<Auftrag> findActiveOrdersByLineId(String lineId, LocalDate when);

    /**
     * Ermittelt alle Hurrican Auftr&auml;ge zu einer LineId (=VBZ) sowie einem bestimmten Status.
     *
     * @param lineId        die gesuchte LineID.
     * @param auftragStatus der gesuchte Status.
     * @return Liste mit Hurrican Auftr&auml;gen.
     */
    List<Auftrag> findActiveOrdersByLineIdAndAuftragStatus(String lineId, Long auftragStatus);

    /**
     * Ermittelt alle g&uuml;ltige Hurrican Auftr&auml;gsdaten zu einer LineId (=VBZ).
     *
     * @param lineId die gesuchte LineID.
     * @param auftragStatus
     * @return Liste mit Hurrican Auftr&auml;gsdaten.
     */
    List<AuftragDaten> findAuftragDatenByLineIdAndStatus(final String lineId, final Long... auftragStatus);

}
