/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2011 15:00:00
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchQuery;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Serviceschnittstelle f&uuml;r das Laden und Umziehen von IP-Adressen.
 */
public interface IPAddressService extends ICCService {

    /**
     * Wird fuer einen Service-Callback verwendet, um zu 'fragen', ob die IP Adressen des 'alten' Auftrags uebernommen
     * werden sollen. <br> Als Ergebnis wird ein Objekt vom Typ <code>Boolean</code> erwartet.
     */
    public static final int CALLBACK_ASK_4_IP_ADDRESS_UEBERNAHME = 200;

    /**
     * Ermittelt eine {@link IPAddress} ueber die angegebene ID.
     *
     * @param ipAddressId
     * @return
     * @throws FindException
     */
    IPAddress findIpAddress(Long ipAddressId) throws FindException;

    /**
     * liefert eine List von IPv6-Adress-Pr&auml;fixen für die angegebene Auftragsnummer.
     */
    @Nonnull
    List<IPAddress> findV6PrefixesByBillingOrderNumber(Long billingOrderNumber) throws FindException;

    /**
     * liefert eine List von IPv4-Adressen für die angegebene Auftragsnummer.
     */
    @Nonnull
    List<IPAddress> findV4AddressByBillingOrderNumber(@Nonnull Long billingOrderNumber) throws FindException;

    /**
     * ermittel zu einer Billing Auftragsnummer alle zugewiesenen (MOnline) IP Adressen.
     */
    List<IPAddress> findAssignedIPs4BillingOrder(Long billingOrderNo) throws FindException;

    /**
     * ermittel zu einer Billing Auftragsnummer alle zugewiesenen (MOnline) IP Adressen (ohne Netze!).
     */
    List<IPAddress> findAssignedIPsOnly4BillingOrder(Long billingOrderNo) throws FindException;

    /**
     * ermittel zu einer Billing Auftragsnummer alle zugewiesenen (MOnline) IP Adressen (ausschl. Netze!).
     */
    List<IPAddress> findAssignedNetsOnly4BillingOrder(Long billingOrderNo) throws FindException;

    /**
     * speichert eine IP Adresse.
     */
    IPAddress saveIPAddress(IPAddress toSave, Long sessionId) throws StoreException;

    /**
     * ermittelt aus der MOnline via WS ein neues V4 Netz.
     */
    @Nonnull
    IPAddress assignIPV4(Long auftragId, Reference purpose, Integer netmaskSize, Reference site, Long sessionId)
            throws StoreException;

    /**
     * ermittelt aus der MOnline via WS ein neues V6 Netz.
     */
    @Nonnull
    IPAddress assignIPV6(Long auftragId, Reference purpose, Integer netmaskSize, Reference site, Long sessionId)
            throws StoreException;

    /**
     * ermittelt alle IP-Bl&ouml;cke zu einer Net-Id, die zu gegebenen Datum aktiv ist/war.
     */
    List<IPAddress> findAssignedIPs4NetId(Long netId, Date dateActive) throws FindException;

    /**
     * ermittelt alle IP-Bl&ouml;cke zu einer Net-Id (sowohl aktive als inaktive)
     */
    List<IPAddress> findIPs4NetId(Long netId) throws FindException;

    /**
     * ermittelt alle IP-Addressbloecke fuer die angegebene technische Auftragsnummer (Hurrican).
     */
    List<IPAddress> findAssignedIPs4TechnicalOrder(Long technicalOrderNo) throws FindException;

    /**
     * ermittelt alle fixen aus der MOnline zugewiesenen IP-Adressen fuer eine gegebene technische Auftragsnummer
     * (Hurrican).
     */
    List<IPAddress> findFixedIPs4TechnicalOrder(Long technicalOrderNo) throws FindException;

    /**
     * ermittelt alle aus der MOnline zugewiesenen Netze fuer eine gegebene technische Auftragsnummer (Hurrican).
     */
    List<IPAddress> findNets4TechnicalOrder(Long technicalOrderNo) throws FindException;

    /**
     * zieht eine aus der MOnline zugewiesene IP Adresse um. Dabei wird die alte Adresse zu heute beendet, eine Kopie
     * der alten mit Startdatum zu heute angelegt und die Billing Auftragsnummer umgeschrieben.
     */
    IPAddress moveIPAddress(IPAddress ipAddress, Long billingOrderNo, Long sessionId) throws StoreException;

    /**
     * gibt die IP-Adressen falls moeglich ab sofort frei, andernfalls wird die IP Adresse stillgelegt (gueltigBis zu
     * heute)
     *
     * @param ipAddrId
     * @return Warnings für alle IP-Adressen die nicht in MOnline freigegeben wurden, weil sie mehreren Aufträgen
     * zugeordnet sind
     * @throws StoreException
     */
    AKWarnings releaseIPAddressesNow(Collection<IPAddress> ipAddrs, Long sessionId) throws StoreException;

    /**
     * prüft ob die gegebene IP Adresse aktiv! noch mindestens einem anderen Auftrag zugeordnet ist (stillgelegte werden
     * ignoriert).
     */
    public AKWarnings ipIsAssignedOnceOnly(IPAddress ipAddr);

    /**
     * Gibt IP-Addressen in Monline frei. Hierzu prueft die Methode, welche IP-Adresse keinem aktiven Auftrag mehr
     * zugeordnet ist und ob ggf. die Karenzzeit bereits abgelaufen ist (bei Kündigungen).
     *
     * @return AKWArnings im Erfolgsfall leer, im Fehlerfall Meldungen
     */
    AKWarnings releaseIPAddresses(Long sessionId);

    /**
     * @return Net-Ids mit zugehörigem Pool-Name aus monline
     */
    List<Pair<Long, Reference>> findAllNetIdsWithPurposeFromMonline() throws FindException;

    /**
     * ermittelt alle IP Adressen zu einer Billing Auftrags No (auch zur Freigabe vorgemerkte sowie bereits freigegebene
     * Adressen). Berechnet ggf. die restliche Karenzzeit
     *
     * @return alle ermittelten IP Adress Views
     */
    List<IPAddressPanelView> findAllIPAddressPanelViews(Long billingOrderNo) throws FindException;

    /**
     * Sucht nach IP Netzen und Adressen ueber die binaere Repraesentation
     *
     * @param filterIPV6           wenn true werden nur IPV6 Typen gefiltert, andernfalls nur IPV4 Typen
     * @param binaryRepresentation binaere Repraesentation des zu filternden IP Ausdrucks
     * @return alle ermittelten IP Adressen als View
     */
    List<IPAddressSearchView> filterIPsByBinaryRepresentation(IPAddressSearchQuery searchQuery) throws FindException;

    /**
     * Ermittelt das DHCPv6-PD Praefix (es ist nur eines pro Auftrag erlaubt) fuer einen Billingauftrag. Die bei einem
     * fehlerhaften Resultset geworfene {@link FindException} fuehrt nicht zu einem Rollback der Transaktion (siehe
     * Konfig in der CCDefaultServices.xml). Soll dies in einem Anwendungsfall aber so gewuenscht sein, so muss der
     * Aufrufer dafuer sorgen!
     *
     * @return DHCPv6_PD Prefix, wenn mehr als ein Prefix existiert fliegt eine HibernateException
     * @throws FindException wenn das ResultSet != 0 und != 1 ist
     */
    IPAddress findDHCPv6PDPrefix(Long billingOrderNo) throws FindException;

    /**
     * Ermittelt das DHCPv6-PD Praefix (es ist nur eines pro Auftrag erlaubt) fuer einen techn. Auftrag. Die bei einem
     * fehlerhaften Resultset oder nicht ermittelbarer {@link Auftragdaten} geworfene {@link FindException} fuehrt nicht
     * zu einem Rollback der Transaktion (siehe Konfig in der CCDefaultServices.xml). Soll dies in einem Anwendungsfall
     * aber so gewuenscht sein, so muss der Aufrufer dafuer sorgen!
     *
     * @return DHCPv6_PD Prefix, wenn mehr als ein Prefix existiert fliegt eine HibernateException
     * @throws FindException wenn das ResultSet != 0 und != 1 oder die {@link AuftragDaten} nicht ermittelbar sind
     */
    IPAddress findDHCPv6PDPrefix4TechnicalOrder(Long auftragId) throws FindException;

}

