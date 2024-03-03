/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2007 13:37:09
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import javax.annotation.*;
import javax.inject.*;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;


public abstract class AbstractIpCommand extends AbstractLeistungCommand {

    @Inject
    private CCAuftragService auftragService;
    @Inject
    private ProduktService produktService;
    @Inject
    protected ReferenceService referenceService;
    @Inject
    protected IPAddressService ipAddressService;
    @Inject
    private BillingAuftragService billingAuftragService;
    @Inject
    private AvailabilityService availabilityService;
    @Inject
    private HVTService hvtService;
    @Inject
    private NiederlassungService niederlassungService;

    @Nonnull
    abstract protected List<IPAddress> getIpAddresses(@Nonnull Long auftragNoOrig) throws FindException;

    @Nonnull
    abstract protected IPAddress assignIP(@Nonnull Long auftragId, @Nonnull Reference purpose,
            @Nonnull Integer netmaskSize, @Nonnull Reference site, @Nonnull Long sessionId) throws StoreException;

    abstract protected boolean isV4();

    @CheckForNull
    abstract protected Reference getPurpose(@Nonnull Produkt produkt) throws FindException;

    @Override
    public Object execute() throws Exception {
        final AuftragDaten auftragDaten = findAuftragDaten();
        final List<IPAddress> addresses = getIpAddresses(auftragDaten.getAuftragNoOrig());
        if (!addresses.isEmpty()) {
            // IpV6-Prefix oder IpV4-Einzeladresse bereits vorhanden
            return ok();
        }

        final Produkt produkt = produktService.findProdukt4Auftrag(getAuftragId());
        final Reference purpose = findPurpose(produkt);
        final Integer netmaskSize = findNetmaskSizeFromProduct(produkt);
        final Reference site = findNiederlassung(auftragDaten, produkt);
        final IPAddress address = assignIP(getAuftragId(), purpose, netmaskSize, site, getSessionId());
        ipAddressService.saveIPAddress(address, getSessionId());

        return ok();
    }

    /**
     * @param produkt
     * @return
     * @throws FindException
     * @throws HurricanServiceCommandException
     */
    private Reference findPurpose(final Produkt produkt) throws FindException, HurricanServiceCommandException {
        final Reference purpose = getPurpose(produkt);
        if (purpose == null) {
            throw new HurricanServiceCommandException(
                    String.format(
                            "Ip%s-Adresse kann nicht reserviert werden, da kein IP-Verwendungszweck fuer das Produkt mit Nr = %s definiert ist.",
                            isV4() ? "V4" : "V6-Praefix", produkt.getProdId())
            );
        }
        return purpose;
    }

    private AuftragDaten findAuftragDaten() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftrag = auftragService.findAuftragDatenByAuftragIdTx(getAuftragId());
        if ((auftrag == null) || (auftrag.getAuftragNoOrig() == null) || (auftrag.getAuftragNoOrig().intValue() == 0)) {
            throw new HurricanServiceCommandException(
                    String.format(
                            "Ip%s-Adresse kann nicht reserviert werden, da der Billing-Auftrag für den technischen Auftrag %d nicht ermittelt werden kann.",
                            isV4() ? "V4" : "V6-Praefix", getAuftragId())
            );
        }
        return auftrag;
    }

    private Integer findNetmaskSizeFromProduct(final Produkt produkt) throws FindException,
            HurricanServiceCommandException {
        Integer netmaskSize = (isV4()) ? produkt.getIpNetmaskSizeV4() : produkt.getIpNetmaskSizeV6();

        if ((netmaskSize == null) || (netmaskSize.intValue() == 0)) {
            throw new HurricanServiceCommandException(
                    String.format(
                            "Ip%s-Adresse kann nicht reserviert werden, da die Netmask fuer das Produkt mit Nr = %s nicht definiert ist.",
                            isV4() ? "V4" : "V6-Praefix", produkt.getProdId())
            );
        }
        return netmaskSize;
    }

    private Reference findNiederlassung(final AuftragDaten auftragDaten, final Produkt produkt) throws FindException,
            HurricanServiceCommandException {
        Adresse address = billingAuftragService
                .findAnschlussAdresse4Auftrag(auftragDaten.getAuftragNoOrig(), Endstelle.ENDSTELLEN_TYP_B);
        GeoId geoId = availabilityService.findGeoId(address.getGeoId());
        List<GeoId2TechLocation> possibleGeoId2TechLocations =
                availabilityService.findPossibleGeoId2TechLocations(geoId, produkt.getId());
        if (CollectionTools.isNotEmpty(possibleGeoId2TechLocations)) {
            HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(
                    possibleGeoId2TechLocations.get(0).getHvtIdStandort());
            Niederlassung niederlassung = niederlassungService.findNiederlassung(hvtGruppe.getNiederlassungId());
            return niederlassung.getIpLocation();
        }
        throw new HurricanServiceCommandException(
                String.format(
                        "Ip%s-Adresse kann nicht reserviert werden, da keine Techlocation fuer die GeoId = %s definiert ist.",
                        isV4() ? "V4" : "V6-Praefix", address.getGeoId())
        );
    }

    private ServiceCommandResult ok() {
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }
}
