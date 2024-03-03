/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 15:07:07
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface fuer die Verwaltung von VPN-Auftraegen.
 *
 *
 */
public interface VPNService extends ICCService {

    /**
     * Sucht nach allen VPN-Vertraegen.
     *
     * @return Liste mit Objekten des Typs <code>VPNVertragView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<VPNVertragView> findVPNVertraege() throws FindException;

    /**
     * Sucht nach einem best. VPN-Eintrag.
     *
     * @param vpnId ID des gesuchten VPN.
     * @return VPN oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public VPN findVPN(Long vpnId) throws FindException;

    /**
     * Ermittelt einen VPN-Eintrag ueber die VPN-Nr. Bei der VPN-Nr handelt es sich um die Taifun-Auftragsnummer des VPN
     * Rahmenvertrags.
     *
     * @param vpnNr
     * @return
     * @throws FindException
     */
    public VPN findVPNByVpnNr(Long vpnNr) throws FindException;

    /**
     * Sucht nach einem VPN ueber den VPN-Namen.
     *
     * @param name Name des zu suchenden VPNs
     * @return VPN oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public VPN findVPNByName(String name) throws FindException;

    /**
     * Sucht nach dem VPN, der einem best. Auftrag zugeordnet ist.
     *
     * @param ccAuftragId ID des Auftrags, dessen VPN gesucht wird.
     * @return VPN oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public VPN findVPNByAuftragId(Long ccAuftragId) throws FindException;

    /**
     * Speichert das VPN-Objekt.
     *
     * @param toSave zu speicherndes Objekt.
     * @throws StoreException
     */
    public void saveVPN(VPN toSave) throws StoreException;

    /**
     * Ordnet einen Auftrag einem VPN zu. <br> Die Objekte auftragTechnik und auftragFaktura koennten auch ueber die
     * entsprechenden Services von den Clients selbst gespeichert werden. Diese Methode bietet jedoch den Vorteil, dass
     * Transaktionen verwendet werden.
     *
     * @param vpnId          ID des VPNs
     * @param auftragTechnik AuftragTechnik des Auftrags, der dem VPN zugeordnet werden soll
     * @throws StoreException wenn bei der Zuordnung ein Fehler auftritt.
     */
    public void addAuftrag2VPN(Long vpnId, AuftragTechnik auftragTechnik)
            throws StoreException;

    /**
     * Entfernt einen Auftrag aus dem VPN. <br> Die Methode besitzt ein Transaktions-Handling. Nur wenn alle
     * Speicher-Vorgaenge erfolgreich sind, wird die Transaktion abgeschlossen - andernfalls 'rollback'.
     *
     * @param auftragTechnik
     * @throws StoreException
     */
    public void removeAuftragFromVPN(AuftragTechnik auftragTechnik)
            throws StoreException;

    /**
     * Sucht nach der aktuellen VPN-Konfiguration fuer einen bestimmten Auftrag.
     *
     * @param ccAuftragId ID des CC-Auftrags, dessen VPN-Konfiguration gesucht wird.
     * @return VPNKonfiguration oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public VPNKonfiguration findVPNKonfiguration4Auftrag(Long ccAuftragId) throws FindException;

    /**
     * Speichert eine VPN-Konfiguration. <br> Ueber das Flag <code>makeHistory</code> wird entschieden, ob ein
     * bestehender Datensatz historisiert werden soll.
     *
     * @param toSave      zu speicherndes Objekt
     * @param makeHistory Flag fuer die Historisierung
     * @return Abhaengig von makeHistory wird entweder <code>toSave</code> oder eine neue Instanz von
     * <code>VPNKonfiguration</code> zurueck gegeben.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public VPNKonfiguration saveVPNKonfiguration(VPNKonfiguration toSave, boolean makeHistory)
            throws StoreException;

    /**
     * Erstellt einen Report mit der Uebersicht ueber alle Auftraege, die einem best. VPN zugeordnet sind.
     *
     * @param vpnId ID des VPNs
     * @return JasperPrint-Objekt
     * @throws AKReportException
     */
    public JasperPrint reportVPNUebersicht(Long vpnId) throws AKReportException;

    /**
     * @param vpnId ID des VPNs
     * @param data  Liste mit den Auftragsdaten, die dem VPN zugeordnet sind.
     * @return JasperPrint-Objekt
     * @throws AKReportException
     * @see reportVPNUebersicht(Long). Der Methode muessen die Auftraege des VPNs uebergeben. Dies hat den Vorteil, dass
     * die Auftraege nicht mehrmals geladen werden muessen, sollte der Client die Daten bereits 'besitzen'.
     */
    public JasperPrint reportVPNUebersicht(Long vpnId, List<AuftragEndstelleView> data) throws AKReportException;

    /**
     * Erstellt einen Report mit den Account-Daten eines Auftrags, der einem VPN zugeordnet ist.
     *
     * @param auftragId Auftrags-ID
     * @return JasperPrint-Objekt
     * @throws AKReportException
     */
    public JasperPrint reportVPNAccount(Long auftragId) throws AKReportException;
}


