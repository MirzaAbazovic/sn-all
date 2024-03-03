/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 11:06:37
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.Ansprechpartner;


/**
 * DAO-Definition zur Verwaltung von Objekten des Typs <code>Ansprechpartner</code>.
 *
 *
 */
public interface AnsprechpartnerDAO extends FindDAO {

    /**
     * Sucht nach allen Ansprechpartnern zu einem best. Kunden. Der Kunde wird mit der originalen Kundennummer
     * bestimmt.
     *
     * @param kundeNo Kundennummer.
     * @return Liste mit Objekten des Typs <code>Ansprechpartner</code> oder <code>null</code>
     */
    public List<Ansprechpartner> findByKundenNo(final Long kundeNo);

    /**
     * Sucht nach den Ansprechpartnern, die den Kunden mit den Kundennummern <code>kundeNos</code> zugeordnet sind. <br>
     * Wichtig: einem Kunden koennen mehrere Ansprechpartner zugeordnet sein.
     *
     * @param kundeNos Liste mit den Kundennummern, deren Ansprechpartner geladen werden sollen.
     * @return Liste mit Objekten des Typs <code>Ansprechpartner</code>.
     */
    public List<Ansprechpartner> findByKundeNos(List<Long> kundeNos);

}


