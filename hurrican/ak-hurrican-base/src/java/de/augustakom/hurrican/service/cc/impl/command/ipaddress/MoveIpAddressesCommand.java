/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.2013 11:31:10
 */
package de.augustakom.hurrican.service.cc.impl.command.ipaddress;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * Zieht die IP Adressen des Quellauftrages auf den Zielauftrag um. Sind dem Quellauftrag keine IP Adressen zugeordnet,
 * geschieht nichts - auch keine Freigabe der IP Adressen auf dem Zielauftrag. Besitzt der Quellauftrag allerdings IP
 * Adressen, so werden zunaechst eventuell vorhandene IP Adressen auf dem Zielauftrag zu sofort freigegeben. Danach
 * werden die IP Adressen des Quellauftrages auf den Zielauftrag umgezogen. Die Uebernahmen wird nur dann durchgeführt,
 * wenn die beteiligten Auftraege dem gleichen(!) Kunden zugeordnet sind und sich die Billingauftragsnummern
 * unterscheiden.
 */
@CcTxRequired
public class MoveIpAddressesCommand extends AbstractIpAddressCommand {

    private CCAuftragService auftragService;
    private IPAddressService ipAddressService;

    @Override
    public Object execute() throws Exception {
        try {
            if (isSameCustomer() && !isSameBillingOrder() && isMoveNecessary()) {
                moveIpAddresses();
            }
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(String.format("Die IP Adressuebernahme ist fehlgeschlagen: %s ",
                    e.getMessage()), e);
        }
        return null;
    }

    /**
     * Zieht die Adressen um.
     */
    private void moveIpAddresses() throws FindException, ServiceNotFoundException, StoreException {
        Long billingNoSrc = getBillingOrderNoSrc();
        Long billingNoDest = getBillingOrderNoDest();
        List<IPAddress> ipAddressesSrc = getIpAddressService().findAssignedIPs4BillingOrder(billingNoSrc);
        releaseIpAddressesDest(billingNoDest);
        for (IPAddress ipAddress : ipAddressesSrc) {
            getIpAddressService().moveIPAddress(ipAddress, billingNoDest, getSessionId());
        }
    }

    /**
     * Gibt die IP Adressen des Zielauftrages unverzueglich frei
     */
    private void releaseIpAddressesDest(Long billingNoDest) throws FindException, ServiceNotFoundException,
            StoreException {
        List<IPAddress> ipAddressesDest = getIpAddressService().findAssignedIPs4BillingOrder(billingNoDest);
        if (CollectionUtils.isNotEmpty(ipAddressesDest)) {
            AKWarnings releaseWarnings = getIpAddressService().releaseIPAddressesNow(ipAddressesDest, getSessionId());
            if (releaseWarnings.isNotEmpty()) {
                getWarnings().addAKWarnings(releaseWarnings);
            }
        }
    }

    /**
     * Ueberprueft, ob der Ursprungs- und Ziel-Auftrag dem gleichen Kunden zugeordnet sind.
     */
    protected boolean isSameCustomer() throws FindException, ServiceNotFoundException {
        Auftrag aSrc = getAuftragService().findAuftragById(getAuftragIdSrc());
        Auftrag aDest = getAuftragService().findAuftragById(getAuftragIdDest());
        return (aSrc != null) && (aDest != null) && NumberTools.equal(aSrc.getKundeNo(), aDest.getKundeNo());
    }

    /**
     * Ueberprueft, ob Quelle und Ziel die gleiche Billingauftragsnummer sind.
     */
    protected boolean isSameBillingOrder() throws FindException {
        Long billingNoSrc = getBillingOrderNoSrc();
        Long billingNoDest = getBillingOrderNoDest();
        return (billingNoSrc != null) && (billingNoDest != null) && NumberTools.equal(billingNoSrc, billingNoDest);
    }

    /**
     * Prueft, ob eine Uebernahmen notwendig ist. Genauer, ob mindestens eine IP Adresse auf dem alten Auftrag gezogen
     * ist.
     */
    protected boolean isMoveNecessary() throws FindException, ServiceNotFoundException {
        Long billingNoSrc = getBillingOrderNoSrc();
        List<IPAddress> ipAddresses = getIpAddressService().findAssignedIPs4BillingOrder(billingNoSrc);
        return ((ipAddresses != null) && (!ipAddresses.isEmpty()));
    }

    /**
     * Ermittelt die Billingauftragsnummer der Quelle.
     */
    protected Long getBillingOrderNoSrc() throws FindException {
        AuftragDaten auftragDaten = getAuftragDatenTx(getAuftragIdSrc());
        if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) {
            throw new FindException(
                    String.format("Die Billingauftragsnummer des Quellauftrages %d konnte nicht ermittelt werden!",
                            getAuftragIdSrc())
            );
        }
        return auftragDaten.getAuftragNoOrig();
    }

    /**
     * Ermittelt die Billingauftragsnummer des Ziels.
     */
    protected Long getBillingOrderNoDest() throws FindException {
        AuftragDaten auftragDaten = getAuftragDatenTx(getAuftragIdDest());
        if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) {
            throw new FindException(
                    String.format("Die Billingauftragsnummer des Zielauftrages %d konnte nicht ermittelt werden!",
                            getAuftragIdDest())
            );
        }
        return auftragDaten.getAuftragNoOrig();
    }

    protected CCAuftragService getAuftragService() throws ServiceNotFoundException {
        if (auftragService == null) {
            auftragService = getCCService(CCAuftragService.class);
        }
        return auftragService;
    }

    /**
     * Setter fuer die Tests
     */
    void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    protected IPAddressService getIpAddressService() throws ServiceNotFoundException {
        if (ipAddressService == null) {
            ipAddressService = getCCService(IPAddressService.class);
        }
        return ipAddressService;
    }

    /**
     * Setter fuer die Tests
     */
    void setIpAddressService(IPAddressService ipAddressService) {
        this.ipAddressService = ipAddressService;
    }
}


