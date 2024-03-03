/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 10:00:03
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.VerbindungsBezeichnungHistoryView;

/**
 * DAO-Interface fuer Objekte des Typs <code>VerbindungsBezeichnung</code> und <code>VBZZaehler</code>.
 *
 *
 */
public interface VerbindungsBezeichnungDAO extends ByExampleDAO, StoreDAO, FindDAO {

    /**
     * Sucht nach der VerbindungsBezeichnung, die einem best. Auftrag zugeordnet ist.
     *
     * @param ccAuftragId ID des CC-Auftrags, dessen VerbindungsBezeichnung gesucht wird.
     * @return VerbindungsBezeichnung oder <code>null</code>
     */
    VerbindungsBezeichnung findByAuftragId(Long ccAuftragId);

    /**
     * Funktion liefert alle VBZs zu einem Kunden
     *
     * @param kundeNo
     * @return
     *
     */
    List<VerbindungsBezeichnung> findVerbindungsBezeichnungenByKundeNo(Long kundeNo);

    /**
     * Sucht nach allen VBZs, die den String <code>verbindungsBezeichnung</code> beinhalten.
     *
     * @param vbz Gesamte oder Teil der gesuchten VBZs.
     * @return Liste mit Objekten des Typs <code>VerbindungsBezeichnung</code>.
     */
    List<VerbindungsBezeichnung> findVerbindungsBezeichnungLike(String vbz);

    /**
     * Ermittelt den naechsten Wert fuer die eindeutige Codierung der Verbindungsbezeichnung.
     *
     * @return naechster Wert fuer die eindeutige Codierung
     */
    Integer getNextUniqueCode();

    /**
     * Sucht nach allen Auftraegen, die einer best. VerbindungsBezeichnung zugeordnet sind.
     *
     * @param vbz Bezeichnung der VerbindungsBezeichnung.
     * @return Liste mit Objekten des Typs <code>VerbindungsBezeichnungHistoryView</code>.
     */
    List<VerbindungsBezeichnungHistoryView> findVerbindungsBezeichnungHistory(String vbz);

    /**
     * Ermittelt den naechsten Wert fuer eine eindeutige LineId.
     *
     * @return naechster Wert
     */
    Integer getNextLineId();

    /**
     * Loescht die uebergebene Verbindungsbezeichnung.
     *
     * @param vbz
     */
    void delete(VerbindungsBezeichnung vbz);

    /**
     * Ermittelt den naechsten Wert, der fuer die Generierung einer eindeutigen WBCI-LineId verwendet wird.
     *
     * @return naechster Wert fuer die eindeutige WBCI-LineId.
     */
    Integer getNextWbciLineIdValue();

}
