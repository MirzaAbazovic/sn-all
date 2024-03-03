/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 15:39:11
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.common.tools.messages.AKMessages;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.ProduktEQConfig;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.query.RangierungsAuftragBudgetQuery;
import de.augustakom.hurrican.model.cc.view.RangierungsAuftragBudgetView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface fuer die Administration von Rangierungen.
 *
 *
 */
public interface RangierungAdminService extends ICCService {

    /**
     * Ermittelt eine Liste mit allen noch nicht erledigten Rangierungs-Auftraegen. Ein Rangierungsauftrag ist so lange
     * nicht erledigt, bis von der Technik ein Ausfuehrungstermin eingetragen ist.
     *
     * @return Liste mit den offenen Rangierungsauftraegen (Objekte vom Typ <code>RangierungsAuftrag</code>).
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<RangierungsAuftrag> findUnfinishedRAs() throws FindException;

    /**
     * Ermittelt einen Rangierungs-Auftrag ueber dessen ID.
     *
     * @param raId ID des gesuchten Rangierungs-Auftrags.
     * @return Objekt vom Typ <code>RangierungsAuftrag</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public RangierungsAuftrag findRA(Long raId) throws FindException;

    /**
     * Speichert den angegebenen Rangierungs-Auftrag ab.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    public void saveRangierungsAuftrag(RangierungsAuftrag toSave) throws StoreException;

    /**
     * Gibt die Rangierungen zu dem Rangierungs-Auftrag mit der angegebenen ID frei. <br> Zusaetzlich wird der Status
     * der zugehoerigen Equipments auf 'rangiert' gesetzt.
     *
     * @param raId       ID des Rangierungs-Auftrags
     * @param technikStd Anzahl der geleisteten Stunden
     * @param sessionId  Session-ID des Users
     * @return der geaenderte Rangierungs-Auftrag
     * @throws FindException  wenn bei der Ermittlung der Daten ein Fehler auftritt.
     * @throws StoreException wenn bei der Freigabe der Rangierungen ein Fehler auftritt.
     *
     */
    public RangierungsAuftrag releaseRangierungen4RA(Long raId, Float technikStd, Long sessionId) throws FindException, StoreException;

    /**
     * Storniert den Rangierungs-Auftrag. Dies geht jedoch nur so lange, wie noch keine Rangierungen definiert wurden.
     *
     * @param raId      ID des zu stornierenden Rangierungs-Auftrags.
     * @param sessionId Session-ID des Users
     * @throws StoreException wenn beim Storno ein Fehler auftritt oder der Auftrag nicht mehr storniert werden
     *                        kann/darf.
     *
     */
    public void cancelRA(Long raId, Long sessionId) throws StoreException;

    /**
     * Abhaengig vom Rangierungs-Auftrag bzw. den darin definierten Physiktypen wird ermittelt, welche Equipments
     * benoetigt werden. - zwei Physiktypen --> alle Equipments laden - ein Physiktyp - ist nicht 2H/4H  --> DSLAM
     * sperren, EWSD u. Carrier laden - ist 2H oder 4H   --> EWSD u. DSLAM sperren, Carrier laden - ist DSLonly      -->
     * EWSD sperren, DSLAM u. Carrier laden
     *
     * @param ra Rangierungs-Auftrag, zu dem die notwendigen Equipments ermittelt werden sollen.
     * @return Array mit boolean-Werten. <br> - Index 0: EWSD (Flag gibt an, ob die EWSD-Equipments notwendig sind) <br>
     * - Index 1: DSLAM (Flag gibt an, ob die DSLAM-Equipments notwendig sind) <br> - Index 2: Carrier (Flag gibt an, ob
     * die Carrier-Equipments notwendig sind) <br>
     *
     */
    public boolean[] validateNeededEquipments(RangierungsAuftrag ra);

    /**
     * Generiert aus den angegebenen Equipments die Rangierungen.
     *
     * @param raId          ID des zugehoerigen Rangierungs-Auftrags
     * @param eqIdsEWSD     Liste mit den Equipment-IDs fuer die EWSD-Seite
     * @param eqIdsCarrier  Liste mit den Equipment-IDs fuer die Carrier-Seite
     * @param eqIdsDSLAMIn  Liste mit den Equipment-IDs fuer die IN-Seite des DSLAMs
     * @param eqIdsDSLAMOut Liste mit den Equipment-IDs fuer die Out-Seite des DSLAMs
     * @param rangSSType    Angabe von 'rangSSType' fuer den Carrier-Stift (nur notwendig, wenn Carrier-Stift rangiert
     *                      wird)
     * @param uetv          Angabe von 'uetv' fuer den Carrier-Stift (nur notwendig, wenn Carrier-Stift rangiert wird)
     * @param iaNumber      (optional) zu verwendende IA-Nummer fuer zu erstellende Innenauftraege (ist notwendig, wenn
     *                      eine Baugruppe noch nicht eingebaut ist)
     * @param sessionId     Session-ID des Benutzers.
     * @return Informations- oder Warnmeldungen die auf Probleme bei der Rangierungserstellung hinweisen.
     * @throws StoreException wenn die Rangierungen nicht erstellt werden konnten.
     *
     */
    public AKMessages createRangierungen(Long raId, List<Long> eqIdsEWSD, List<Long> eqIdsCarrier,
            List<Long> eqIdsDSLAMIn, List<Long> eqIdsDSLAMOut, String rangSSType, Uebertragungsverfahren uetv, String iaNumber,
            Long sessionId)
            throws StoreException;

    /**
     * Ermittelt alle Rangierungen, die fuer den Rangierungsauftrag mit der angegebenen ID erstellt wurden.
     *
     * @param raId ID des Rangierungsauftrags.
     * @return Liste mit Objekten des Typs <code>Rangierung</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<Rangierung> findRangierungen4RA(Long raId) throws FindException;

    /**
     * Erstellt die Rangierungsliste fuer einen bestimmten Rangierungsauftrag.
     *
     * @param raId ID des Rangierungsauftrags.
     * @return JasperPrint-Objekt
     * @throws AKReportException wenn beim Erstellen des Reports ein Fehler auftritt.
     *
     */
    public JasperPrint printRangierungsliste(Long raId) throws AKReportException;

    /**
     * Ermittelt div. Daten von Rangierungs-Auftraegen und den zugehoerigen Budgets an Hand der Filter-Parameter aus dem
     * Query-Objekt.
     *
     * @param query Query-Objekt mit den Filter-Parametern
     * @return Liste mit Objekten des Typs <code>RangierungsAuftragBudgetView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<RangierungsAuftragBudgetView> findRABudgetViews(RangierungsAuftragBudgetQuery query)
            throws FindException;

    /**
     * Speichert das angegebene ProduktEQConfig-Objekt. <br>
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    public void saveProduktEQConfig(ProduktEQConfig toSave) throws StoreException;

    /**
     * Loescht das angegebene Konfigurations-Objekt.
     *
     * @param toDelete zu loeschendes Objekt
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt.
     *
     */
    public void deleteProduktEQConfig(ProduktEQConfig toDelete) throws DeleteException;

    /**
     * Ermittelt alle ProduktEQConfig-Objekte zu einem bestimmten Produkt.
     *
     * @param prodId Produkt-ID
     * @return Liste mit Objekten des Typs <code>ProduktEQConfig</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<ProduktEQConfig> findProduktEQConfigs(Long prodId) throws FindException;

    /**
     * Ermittelt alle Equipment-Konfigurationen zu einem Produkt und erstellt aus diesen Example Objekte des Typs
     * 'Equipment'. <br> Ueber die Gruppierung der ProduktEQConfig Objekte (Parameter 'configGroup' und 'eqTyp') kann es
     * dabei sein, dass aus mehreren Konfigurationsobjekten ein Example-Objekt entsteht.
     *
     * @param prodId                    Produkt-ID
     * @param eqType                    (optional) hier kann eingeschraenkt werden, von welcher Equipment-Seite (IN od.
     *                                  OUT) die Konfig geladen werden soll. Eine Angabe von 'null' fuehrt dazu, dass
     *                                  beide Seiten geladen werden. (Als Typ wird eine Konstante aus ProduktEQConfig
     *                                  erwartet.)
     * @param rangierungsPartDefault    Flag, ob die Daten fuer den ersten Rangierungs-Teil ermittelt werden sollen
     * @param rangierungsPartAdditional Flag, ob die Daten fuer den zweiten Rangierungs-Teil ermittelt werden sollen
     * @return Liste mit Objekten des Typs <code>Equipment</code>, die als Example dienen koennen.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<Equipment> createExampleEquipmentsFromProdEqConfig(Long prodId, String eqType,
            boolean rangierungsPartDefault, boolean rangierungsPartAdditional) throws FindException;

    /**
     * Generiert aus den angegebenen Equipments SDH/PDH Rangierungen.
     *
     * @param raId         ID des zugehoerigen Rangierungs-Auftrags
     * @param eqIdsSDH     Liste mit den Equipment-IDs fuer die SDH-Seite
     * @param eqIdsCarrier Liste mit den Equipment-IDs fuer die Carrier-Seite
     * @param eqIdsPDHIn   Liste mit den Equipment-IDs fuer die IN-Seite der PDH-Baugruppe
     * @param eqIdsPDHOut  Liste mit den Equipment-IDs fuer die Out-Seite der PDH-Baugruppe
     * @param rangSSType   Angabe von 'rangSSType' fuer den Carrier-Stift (nur notwendig, wenn Carrier-Stift rangiert
     *                     wird)
     * @param uetv         Angabe von 'uetv' fuer den Carrier-Stift (nur notwendig, wenn Carrier-Stift rangiert wird)
     * @param sessionId    Session-ID des Benutzers.
     * @return Informations- oder Warnmeldungen die auf Probleme bei der Rangierungserstellung hinweisen.
     * @throws StoreException
     */
    public AKMessages createSDHPDHRangierung(Long id, List<Long> eqIdsSDH,
            List<Long> eqIdsCarrier, List<Long> eqIdsPDHIn, List<Long> eqIdsPDHOut,
            String rangSSType, Uebertragungsverfahren uetv, Long sessionId) throws StoreException;

}


