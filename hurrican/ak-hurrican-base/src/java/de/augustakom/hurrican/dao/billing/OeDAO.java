/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 08:42:35
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.OE;


/**
 * Interface zur Definition von speziellen DAO-Methoden zur Verwaltung von Objekten des Typs <code>OE</code>
 *
 *
 */
public interface OeDAO {

    /**
     * Sucht nach der OE, die die originale OE-No. <code>oeNoOrig</code> besitzt. <br>
     *
     * @param oeNoOrig originale OE-No, nach der gesucht werden soll.
     * @return
     */
    public OE findByOeNoOrig(final Long oeNoOrig);

    /**
     * Sucht nach den Produkt-Namen zu den Auftraegen mit den (original) Auftrags-Nummer, die in
     * <code>auftragNoOrigs</code> angegeben sind.
     *
     * @param auftragNoOrigs
     * @return Map. Als Key werden die Auftrag-Nos, als Value der zugehoerige Produkt-Name verwendet.
     */
    public Map<Long, String> findProduktNamen4Auftraege(List<Long> auftragNoOrigs);

    /**
     * Sucht nach dem Produkt-Namen zu einem Auftrag mit der (orirginal) Auftrags-Nummer die in
     * <code>auftragNoOrig</code> angegeben ist.
     *
     * @param auftragNoOrig
     * @return String mit dem Produkt-Namen
     */
    public String findProduktName4Auftrag(Long auftragNoOrig);

    /**
     * Sucht nach der vaterOeNoOrig zu einem Auftrag mit der (orirginal) Auftrags-Nummer die in
     * <code>auftragNoOrig</code> angegeben ist.
     *
     * @param auftragNoOrig
     * @return Long mit der VaterOeNoOrig
     */
    public Long findVaterOeNoOrig4Auftrag(final Long auftragNoOrig);

    /**
     * Sucht nach allen Einträgen in Tablle OE mit bestimmten <code>oeTyp</code>
     *
     * @param oeTyp nach dem gesucht werden soll
     * @return
     */
    public List<OE> findAllByOetyp(String oeTyp);

}


