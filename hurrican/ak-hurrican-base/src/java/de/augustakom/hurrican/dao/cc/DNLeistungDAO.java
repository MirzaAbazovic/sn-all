/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2005 09:17:36
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Lb2Leistung;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung2Parameter;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;
import de.augustakom.hurrican.model.cc.view.LeistungInLeistungsbuendelView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * DAO-Interface fuer die Verwaltung von Rufnummern-Leistungen.
 *
 *
 */
public interface DNLeistungDAO extends ByExampleDAO, StoreDAO, FindAllDAO, FindDAO {

    /**
     * Sucht nach allen Leistungen, die den angegebenen Rufnummern zugeordnet sind.
     *
     * @param dnNos              Liste mit den Rufnummer-IDs deren Leistungen ermittelt werden sollen.
     * @param leistungsbuendelId (optional) ID des zugehoerigen Leistungsbuendels
     * @return Liste mit Objekten des Typs <code>DNLeistungView</code>
     */
    List<DNLeistungsView> findLeistungViews(List<Long> dnNos, Long leistungsbuendelId);

    /**
     * Sucht nach allen Zuordnungen von Leistungsbuendeln zu der angegebenen Leistungsnummer. Die ermittelten Mappings
     * werden nach der {@link Leistungsbuendel2Produkt#getProtokollLeistungNoOrig()} sortiert. Damit ist sichergestellt,
     * dass Mappings mit angegebener Protokoll-Leistung in der Liste zuerst aufgefuehrt werden!
     *
     * @param leistungNoOrig die Produkt-Leistung
     * @return Liste mit Objekten des Typs <code>Leistungsbuendel2Produkt</code>
     */
    List<Leistungsbuendel2Produkt> findLeistungsbuendel2Produkt(Long leistungNoOrig);

    /**
     * Löscht alle Leistungsbuendel zu dem angegebenen Produkt und Leistung.
     *
     * @param prodOeNoOrig
     * @param leistungNoOrig
     */
    void deleteLeistungsbuendel2Produkt(Long prodOeNoOrig, Long leistungNoOrig);

    /**
     * Speichert alle angegebenen Objekte. <br>
     *
     * @param dnOeNo2Produkt
     * @see saveLeistungsbuendel2Produkt(Leistungsbuendel2Produkt)
     */
    void saveLeistungsbuendel2Produkt(List<Leistungsbuendel2Produkt> dnOeNo2Produkt);

    /**
     * Speichert das angegebene Objekt. <br> Achtung: es wird lediglich eine Insert-Operation verwendet!
     *
     * @param lb2P
     */
    void saveLeistungsbuendel2Produkt(Leistungsbuendel2Produkt lb2P);

    /**
     * Findet alle zuordnungen der Parameter zu Leistungen
     *
     * @return
     */
    List<Leistung2Parameter> findLeistung2Parameter(Long leistungId);

    /**
     * Loescht alle Parameterzuordnungen zur gewaehlten Leistung.
     *
     * @param leistungId
     */
    void deleteLeistung2Parameter(Long leistungId);

    /**
     * Speichert alle angegebenen Mapping-Objekte. <br>
     *
     * @param leistung2OE
     * @see saveParameter2Leistung(Leistung2Parameter)
     */
    void saveLeistung2Parameter(List<Leistung2Parameter> leistung2parameter);

    /**
     * Speichert das angegebene Objekt. <br> Achtung: es wird nur eine Insert-Operation zur Verfuegung gestellt!
     *
     * @param lb2P
     */
    void saveLeistung2Parameter(Leistung2Parameter l2p);

    /**
     * Findet alle Rufnummern-Lesitungen, die einem bestimmten Leistungsbuendel zugeordnet sind
     *
     * @param lbId Leistungsbuendel-ID
     * @return Liste mit den gefundenen Leistungen
     */
    List<Leistung4Dn> findDNLeistungen4Buendel(Long lbId);

    /**
     * Findet die der Leistung zugeordneten Parameter in Klartext nach <code>Leistungid</code>
     *
     * @param lId
     * @return Liste LeistungParameter
     */
    List<LeistungParameter> findSignedParameter2Leistung(Long lId);

    /**
     * findet die einer Rufnummer zugeordneten Leistungen
     *
     * @param dnNos
     * @return Liste vom Typ Leistung2DN
     */
    List<Leistung2DN> findLeistung2DnByDnNos(List<Long> dnNos);

    /**
     * Findet alle {@code Leistung2Dn}-Objekte zu den angegebenen Parametern, deren property "ewsdKuendigung" auf {@code
     * null} gesetzt ist und die somit noch nicht zur Freigabe CPS Provisioniert wurden.
     *
     * @param leistungId Id der Leistung4DN die zu kuendigen ist
     * @param rufnummern Liste der Rufnummern
     * @return
     */
    List<Leistung2DN> findAktiveLeistung2DnByRufnummern(Long leistungId, List<Long> rufnummern);

    /**
     * Ermittelt alle Rufnummern-Leistungen, die noch nicht provisioniert sind (Einrichtung oder Kuendigung) und ein
     * geplantes Aenderungsdatum = provDate besitzen.
     *
     * @param provDate Pruef-Datum
     * @return Liste mit den noch nicht provisionierten Rufnummern-Leistungen
     *
     */
    List<Leistung2DN> findUnProvisionedDNServices(Date provDate);

    /**
     * Ermittelt alle Rufnummernleistungen, die ueber eine bestimmte CPS-Transaction provisioniert (eingerichtet /
     * gekuendigt) wurden.
     *
     * @param cpsTxId ID der CPS-Transaction
     * @return Liste mit den Rufnummernleistungen, die durch die angegebene CPS-Tx provisioniert wurden
     */
    List<Leistung2DN> findLeistung2DN4CPSTx(Long cpsTxId);

    /**
     * loescht alle einer Rufnummer zugeordneten Leistungen
     *
     * @param dnNos
     */
    void deleteLeistung2DnByDnNo(Long dnNo);

    /**
     * Sucht nach allen Default-Leistungen des angegebenen Leistungsbuendels.
     *
     * @param lbId ID des Leistungsbuendels, dessen Default-Leistungen ermittelt werden sollen.
     * @return
     *
     */
    List<Leistung4Dn> findDefaultLeistungen4Buendel(Long lbId);

    /**
     * Findet eine View der Leistungsbuendel und Leistungen zu dem angegebenen Leistungsbuendel.
     */
    List<LeistungInLeistungsbuendelView> findAllLb2Leistung(Long lbId);

    /**
     * Update auf die Tabelle LB2Leistung. Setzt die Felder Gueltig_bis und verwenden_bis
     *
     * @param vBis
     * @param lb2LId
     * @throws StoreException
     */
    void updateLB2Leistung(Date vBis, Long lb2LId);

    /**
     * Ermittelt die Konfiguration einer bestimmten Leistung in einem Leistungsbuendel.
     *
     * @param leistungId         ID der Leistung, deren Konfiguration ermittelt werden soll.
     * @param leistungsbuendelId ID des Leistungsbuendels.
     * @return
     *
     */
    Lb2Leistung findLb2Leistung(Long leistungId, Long leistungsbuendelId);

    /**
     * Liefert ein Liste mit allen Dn_Nos (gruppiert) aus der Tabelle t_leistung_dn
     *
     * @return Liste vom Typ Long
     */
    List<Long> groupedDnNos();
}


