/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 10:55:30
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;


/**
 * DAO-Definition zur Verwaltung von Objekten vom Typ <code>de.augustakom.hurrican.model.billing.Kunde</code>
 *
 *
 */
public interface KundeDAO extends ByExampleDAO, FindDAO {

    /**
     * Sucht nach den Kunden mit den Kundennummern, die in <code>kundeNos</code> angegeben sind.
     *
     * @param kundeNos Liste mit den Kundennummern, der Kunden, die geladen werden sollen.
     * @return Liste mit Objekten des Typs <code>Kunde</code>.
     */
    public List<Kunde> findByKundeNos(List<Long> kundeNos);

    /**
     * Sucht nach allen Kunden, deren (original) Kundennummern sich innerhalb des Arrays <code>kNos</code> befinden.
     * <br> Als Suchparameter wird ausserdem HistStatus=AKT verwendet.
     *
     * @param kNos Array mit den (original) Kundennummern.
     * @return Map mit den Kundendaten. Als Key wird die (original) Kundennummer verwendet, als Value das
     * <code>Kunde</code-Objekt.
     */
    public Map<Long, Kunde> findByKundeNos(Long[] kNos);

    /**
     * Findet alle Kunden, die den Query-Parametern entsprechen.
     *
     * @param query Query mit den Filter-Eigneschaften
     * @return Liste von Kunden-Objekten oder <code>null</code>
     */
    public List<KundeAdresseView> findByQuery(final KundeQuery query);

    /**
     * Gibt eine Liste mit den Kundennummern der Kunden zurueck, die einem best. Haupt-Kunden zugeordnet sind.
     *
     * @param hauptKundeNo ID des Hauptkunden dessen zugeordnete Kunden gesucht werden.
     * @return Liste mit Integer-Objekten
     */
    public List<Long> findKundeNos4HauptKunde(Long hauptKundeNo);

    /**
     * Sucht nach den Kunden-Namen, die den Kundennummern entsprechen. Im Gegensatz zu <code>findByKundeNos(..)</code>
     * wird in dieser Methode nur der Kunden-Name geladen!
     *
     * @param kundeNos
     * @return Instanz einer Map. Als Key werden die Kunde-Nos, als Value der zugehoerige Kunden-Name verwendet.
     */
    public Map<Long, String> findKundenNamen(List<Long> kundeNos);

}


