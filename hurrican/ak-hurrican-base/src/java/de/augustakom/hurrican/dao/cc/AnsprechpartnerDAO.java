/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 11:56:18
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Ansprechpartner.Typ;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.view.CCAnsprechpartnerView;


/**
 * DAO-Interface fuer Ansprechpartner-Entities
 *
 *
 */
public interface AnsprechpartnerDAO extends StoreDAO, FindDAO, ByExampleDAO {

    /**
     * Findet alle Ansprechpartner des gegebenen Typs fuer den gegebenen Auftrag.
     *
     * @param type      Falls {@code null}, werden Ansprechpartner jeden Typs gesucht
     * @param auftragId Darf nicht {@code null} sein
     * @param preferred Falls {@code true}, werden nur bevorzugte Ansprechpartner gesucht
     * @return liste mit Ansprechpartnern (leer, falls keine gefunden wurden)
     */
    List<Ansprechpartner> findAnsprechpartner(Typ type, Long auftragId, boolean preferred);

    /**
     * Loescht einen Ansprechpartner. Die Adresse des Ansprechpartners bleibt bestehen.
     *
     * @param ansprechpartner Der zu loeschende Ansprechpartner
     */
    void deleteAnsprechpartner(Ansprechpartner ansprechpartner);

    /**
     * Liefert alle Ansprechpartner zurueck, die auf die gegebene Adresse referenzieren.
     *
     * @param ccAddress Die Adresse
     * @return Liste von Ansprechpartnern, kann leer sein, nie {@code null}.
     */
    List<Ansprechpartner> findAnsprechpartnerForAddress(CCAddress address);


    /**
     * Sucht nach allen Ansprechpartnern zu einem best. Kunden. Der Kunde wird mit der originalen Kundennummer
     * bestimmt.
     *
     * @param kundeNo Kundennummer.
     * @param buendelNr B&uuml;ndelnummer.
     * @return Liste mit Objekten des Typs <code>Ansprechpartner</code> oder <code>null</code>
     */
    List<CCAnsprechpartnerView> findAnsprechpartnerByKundeNoAndBuendelNo(final Long kundeNo, final Integer buendelNr);

}
