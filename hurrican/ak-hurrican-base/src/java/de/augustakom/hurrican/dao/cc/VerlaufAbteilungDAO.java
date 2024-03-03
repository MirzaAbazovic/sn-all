/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2004 15:18:46
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>VerlaufAbteilung</code>.
 *
 *
 */
public interface VerlaufAbteilungDAO extends ByExampleDAO, StoreDAO, FindDAO, FindAllDAO {

    /**
     * Sucht nach einem Verlaufs-Eintrag fuer eine best. Abteilung und einem best. uebergeordneten Verlauf.
     *
     * @param verlaufId       ID des uebergeordneten Verlaufs.
     * @param abtId           ID der Abteilung
     * @param niederlassungId (optional) ID der Niederlassung
     * @return Instanz von <code>VerlaufAbteilung</code> oder <code>null</code>.
     */
    public VerlaufAbteilung findByVerlaufAndAbtId(Long verlaufId, Long abtId, Long niederlassungId);

    /**
     * Sucht nach einem Verlaufs-Eintrag fuer einen best. Verlauf und einem best. uebergeordneten Abteilungs-Verlauf.
     *
     * @param verlaufId          ID des uebergeordneten Verlaufs.
     * @param verlaufAbteilungId ID des uebergeordneten Abteilungs-Verlaufs.
     * @return Liste von {@code VerlaufAbteilung}en. Nie {@code null}.
     */
    public List<VerlaufAbteilung> findByVerlaufAndVerlaufAbteilungId(Long verlaufId, Long verlaufAbteilungId);

    List<VerlaufAbteilung> findVerlaufAbteilungen(Long verlaufId, Long... abteilungIds) throws FindException;

    /**
     * Aendert den Status der VerlaufAbteilung-Datensaetze zum Verlauf mit der ID <code>verlaufId</code> auf
     * <code>statusId</code>.
     *
     * @param verlaufId ID des Verlaufs
     * @param statusId  Status fuer die VerlaufAbteilung-Datensaetze
     * @return die Anzahl von angepassten VerlaufAbteilung-Datensaetze
     */
    public int updateStatus(Long verlaufId, Long statusId);

    /**
     * Loescht Abteilungs-Eintraege fuer einen best. Verlauf. <br> Ueber das Flag <code>notScvAndDispo</code> kann
     * gesteuert werden, ob alle oder nur die Datensaetze, die nicht fuer AM und DISPO sind, geloescht werden sollen.
     *
     * @param verlaufId      ID des uebergeordneten Verlaufs.
     * @param notScvAndDispo Flag, ob alle oder alle ausser AM+DISPO geloescht werden sollen (true: AM+DISPO Datensaetze
     *                       bleiben erhalten).
     * @return die Anzahl von geloeschten VerlaufAbteilung-Datensaetze
     */
    public int deleteVerlaufAbteilung(Long verlaufId, boolean notScvAndDispo);

}


