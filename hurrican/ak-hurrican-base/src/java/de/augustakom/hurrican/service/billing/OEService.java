/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 10:50:28
 */
package de.augustakom.hurrican.service.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service-Interface fuer Objekte des Typs <code>OE</code>
 *
 *
 */
public interface OEService extends IBillingService {

    /**
     * Gibt eine Liste von <code>OE</code>-Objekten zurueck.
     */
    public static final int FIND_STRATEGY_ALL = 0;

    /**
     * Such-Strategie, um alle OE zu finden, die eine entsprechung in der Tabell PRODUCT_TYPE haben. Dort wird definiert
     * ob Rufnummern zu diesem OE nötig sind.<br> Ergebnisliste enthaelt Objekte vom Typ <code>OE</code>.
     */
    public static final int FIND_STRATEGY_HAS_DN = 1;

    /**
     * Sucht nach einer best. OE.
     *
     * @param oeNoOrig
     * @return
     * @throws FindException
     *
     */
    public OE findOE(Long oeNoOrig) throws FindException;

    /**
     * Sucht nach den Produkt-Namen zu den Auftraegen mit den (original) Auftrags-Nummer, die in
     * <code>auftragNoOrigs</code> angegeben sind.
     *
     * @param auftragNoOrigs
     * @return Instanz einer Map. Als Key werden die Auftrag-Nos, als Value der zugehoerige Produkt-Name verwendet.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Map<Long, String> findProduktNamen4Auftraege(List<Long> auftragNoOrigs) throws FindException;

    /**
     * Sucht nach dem Produkt-Namen zu einem Auftrag mit der (original) Auftrags-Nummer, die in
     * <code>auftragNoOrig</code> angegeben wird.
     *
     * @param auftragNoOrig
     * @return String mit dem Produkt-Namen
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public String findProduktName4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach der vaterOeNoOrig zu einem Auftrag mit der (original) Auftrags-Nummer, die in
     * <code>auftragNoOrig</code> angegeben wird.
     *
     * @param auftragNoOrig
     * @return Long mit der VaterOeNoOrig
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Long findVaterOeNoOrig4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach den Eintraegen in OE nach den Oe-Typen
     *
     * @param prodCode
     * @return Liste mit ERgebnissen vom Typ OE
     * @throws FindException
     */
    public List<OE> findOEByOeTyp(String prodCode, int strategy) throws FindException;
}


