/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2011 11:08:22
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchView;

/**
 * DAO-Definition fuer Objekte des Typs <code>IPAddress</code>
 */
public interface IPAddressDAO extends FindDAO, ByExampleDAO, StoreDAO {

    /**
     * liefert eine Menge an V6 Pr&auml;fix Adressen f&uml;r einen Billing-Auftrag.
     */
    public List<IPAddress> findV6PrefixesByBillingOrderNumber(Long billingOrderNumber);

    /**
     * liefert eine Menge an V4 Adressen f&uml;r einen Billing-Auftrag.
     */
    @Nonnull
    public List<IPAddress> findV4AddressesByBillingOrderNumber(@Nonnull Long billingOrderNumber);

    /**
     * ermittelt zu einer Billing Auftragsnummer alle aktiven zugewiesenen (MOnline) IP Adressen.
     */
    public List<IPAddress> findAssignedIPs4BillingOrder(Long billingOrderNo);

    /**
     * ermittelt zu einer Billing Auftragsnummer ALLE (aktive und nicht aktive) zugewiesene (MOnline) IP Adressen.
     */
    public List<IPAddress> findAllAssignedIPs4BillingOrder(Long billingOrderNo);

    /**
     * ermittel zu einer Billing Auftragsnummer alle zugewiesenen (MOnline) IP Adressen (ohne Netze!).
     */
    public List<IPAddress> findAssignedIPsOnly4BillingOrder(Long billingOrderNo);

    /**
     * ermittel zu einer Billing Auftragsnummer alle zugewiesenen (MOnline) IP Adressen (ausschl. Netze!).
     */
    public List<IPAddress> findAssignedNetsOnly4BillingOrder(Long billingOrderNo);

    /**
     * ermittlet zu einer Net-Id (Identifikation von Adressbl&ouml;cken in MOnline) und zu einem Datum eine Menge an
     * {@link IPAddress}.
     *
     * @param netId      Die Net-Id aus MOnline als Identifikator eines Adressblocks.
     * @param dateActive Zeitpunkt zu dem aktive IP-Adressbl&ouml;cke ermittelt werden sollen.
     * @return
     */
    public List<IPAddress> findAssignedIPs4NetId(Long netId, Date dateActive);

    /**
     * ermittlet zu einer Net-Id (Identifikation von Adressbl&ouml;cken in MOnline) alle {@link IPAddress}.
     *
     * @param netId Die Net-Id aus MOnline als Identifikator eines Adressblocks.
     * @return alle {@link IPAddress} mit gegebener Net-Id. Ist die Net-Id {@code null}, alle {@link IPAddress}
     */
    public List<IPAddress> findIPs4NetId(Long netId);

    /**
     * Ermittelt die Anzahl an Auftraegen, die einer noch gueltigen IP (gueltigBis > Heute) zugeordnet sind.
     *
     * @param netId die ID des zur IP-Adresse gehoerenden Netzblocks
     */
    public int findIpToOrdersAssignmentCount(Long netId);

    /**
     * ermittelt die Anzahl IP-Address-Entitaeten (gleiche netId), die anderen Auftraegen zugeordnet sind
     *
     * @return Liste der BillingOrderNos
     */
    public List<Long> findActiveIPs4OtherOrders(Long netId, Long billingOrderNo);

    /**
     * ermittelt alle IP Adressen, die noch nicht freigegeben sind (inklusive aktive!)
     */
    public List<IPAddress> findNonReleasedIPs4NetId(Long netId);

    /**
     * sucht nach IP Netzen und Adressen ueber die binaere Repraesentation
     *
     * @param addressTypes Liste der erlaubten Adresstypen
     */
    public List<IPAddressSearchView> filterIPsByBinaryRepresentation(List<String> addressTypes,
            String binaryRepresentation, boolean onlyActive, Integer limit);

    /**
     * ermittelt alle relativen IP Adressen zu einem gegebenen Praefix und dessen Billing Auftragsnummer
     */
    public List<IPAddress> findRelativeIPs4Prefix(IPAddress prefix);

    /**
     * ermittelt alle aktiven (nicht freigegebenen) und aus der MOnline gezogenen IP Adressen
     *
     * @return Map<NetID, Set<IPAddress>>
     */
    public
    @NotNull
    Map<Long, Set<IPAddress>> findAllActiveAndAssignedIPs();

    /**
     * ermittelt das DHCPv6-PD Praefix (es ist nur eines pro Auftrag erlaubt) fuer einen Billingauftrag
     */
    IPAddress findDHCPv6PDPrefix(Long billingOrderNo);

}

