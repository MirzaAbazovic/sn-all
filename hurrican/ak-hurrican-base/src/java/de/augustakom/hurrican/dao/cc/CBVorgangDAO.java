/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.06.2007 11:16:30
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.augustakom.hurrican.model.cc.view.CBVorgangView;

/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>CBVorgang</code>.
 *
 *
 */
public interface CBVorgangDAO extends ByExampleDAO, StoreDAO, FindDAO, FindAllDAO, DeleteDAO {

    /**
     * Ermittelt alle CBVorgangs-Objekte, die zu einer bestimmten Carrierbestellung erfasst sind. <br> Die Sortierung
     * erfolgt in absteigender(!) Reihenfolge der Vorgangs-ID (aktuellster Vorgang an Index 0).
     *
     * @param cbIDs IDs der Carrierbestellung
     * @return Liste mit Objekten des Typs <code>CBVorgang</code>.
     */
    public List<CBVorgang> findCBVorgaenge4CB(Long... cbIDs);

    /**
     * Ermittelt alle noch offenen (Status < CLOSED) CBVorgaenge samt der Niederlassung, <br> die das Wiedervorlage_AM
     * Datum in der Vergangenheit haben. Die Sortierung erfolgt nach dem Vorgabedatum M-net.
     *
     * @return Liste mit Objekten des Typs <code>CBVorgangNiederlassung</code>.
     */
    public List<CBVorgangNiederlassung> findOpenCBVorgaengeNiederlassungWithWiedervorlage();

    /**
     * Ermittelt alle CBVorgaenge von den angegebenen Carriern. <br> Die Ergebnisliste kann ueber die Angabe von Min-
     * und Max-Status eingeschraenkt werden. <br> Die Sortierung erfolgt nach dem Vorgabedatum M-net.
     *
     * @param carrierIds (optional) falls angegeben, werden nur die Vorgaenge der betreffenden Carrier ermittelt. Bei
     *                   Angabe von <code>null</code> werden die Vorgaenge aller Carrier ermittelt.
     * @param minStatus  (optional) minimaler Status, der beruecksichtigt werden soll
     * @param minEqual   (notwendig, wenn minStatus gesetzt) definiert, ob der minStatus mit > (FALSE) oder >= (TRUE)
     *                   gefiltert werden soll. Falls nicht definiert, wird mit > gesucht
     * @param maxStatus  (optional) maximaler Status, der beruecksichtigt werden soll.
     * @param maxEqual   (notwendig, wenn maxStatus gesetzt) definiert, ob der minStatus mit < (FALSE) oder <= (TRUE)
     *                   gefiltert werden soll. Falls nicht definiert, wird mit < gesucht.
     * @return Liste mit Objekten des Typs <code>CBVorgangView</code>.
     */
    public List<CBVorgangView> findOpenCBVorgaenge(List<Long> carrierIds, Long minStatus, Boolean minEqual,
            Long maxStatus, Boolean maxEqual);

    /**
     * Ermittelt die naechste Carrier-Referenz-Nummer.
     */
    public String getNextCarrierRefNr();

    /**
     * Ermittelt die naechste Auftragsklammer-Nummer.
     */
    public Long getNextAuftragsKlammer();

    /**
     * Ermittelt {@link CBVorgang} anhand der uebergebenen {@code carrierRefNr}.
     *
     * @throws NoSuchElementException   falls kein {@link CBVorgang} gefunden
     * @throws IllegalArgumentException falls mehrere {@link CBVorgang}e gefunden
     */
    public CBVorgang findCBVorgangByCarrierRefNr(String carrierRefNr);
}
