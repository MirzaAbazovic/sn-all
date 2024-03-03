/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2008 11:52:57
 */
package de.augustakom.hurrican.service.cc.impl.command;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.view.EndstelleAnsprechpartnerView;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Command-Klasse, um die AccessPoint-Adresse (Standortadresse) vom Billing-System auf eine Hurrican-Endstelle zu
 * uebertragen. <br>
 *
 *
 */
@CcTxRequired
public class CopyAPAddressCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(CopyAPAddressCommand.class);

    /**
     * Key, um dem Command die aktuelle Session-ID zu uebergeben.
     */
    public static final String KEY_SESSION_ID = "session.id";
    /**
     * Key, um dem Command das zu verwendende AuftragDaten-Objekt zu uebergeben.
     */
    public static final String KEY_AUFTRAG_DATEN = "auftrag.daten";

    // Modelle
    private Long sessionId = null;
    private AuftragDaten auftragDaten = null;

    // Services
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    private CCKundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AnsprechpartnerService")
    private AnsprechpartnerService ansprechpartnerService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService prodService;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        checkValues();
        copyAPAddress();
        return null;
    }

    /*
     * Kopiert die im Billing-System erfasste Standortadresse auf die Hurrican-Endstelle.
     *
     * @param ad
     *
     * @throws ServiceCommandException
     *
     * @throws StoreException
     */
    private void copyAPAddress() throws HurricanServiceCommandException {
        try {
            Produkt produkt = prodService.findProdukt(auftragDaten.getProdId());
            if (produkt == null) {
                throw new FindException("Produkt konnte nicht ermittelt werden!");
            }

            if ((auftragDaten.getAuftragNoOrig() == null) || BooleanTools.nullToFalse(produkt.getCreateAPAddress())) {
                return;
            }

            if (NumberTools.equal(produkt.getEndstellenTyp(), Produkt.ES_TYP_A_UND_B)) {
                if (endstellenService.hasAPAddressChanged(auftragDaten, Endstelle.ENDSTELLEN_TYP_A)) {
                    copyAccessPoint(Endstelle.ENDSTELLEN_TYP_A);
                }
                if (endstellenService.hasAPAddressChanged(auftragDaten, Endstelle.ENDSTELLEN_TYP_B)) {
                    copyAccessPoint(Endstelle.ENDSTELLEN_TYP_B);
                }
            }
            else if (NumberTools.equal(produkt.getEndstellenTyp(), Produkt.ES_TYP_NUR_B)
                    && endstellenService.hasAPAddressChanged(auftragDaten, Endstelle.ENDSTELLEN_TYP_B)) {
                copyAccessPoint(Endstelle.ENDSTELLEN_TYP_B);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler beim Kopieren der Endstellendaten aus dem Billing-System!", e);
        }
    }

    /*
     * Abgleich einer Endstellen-Adresse und Anlegen des Ansprechpartners
     */
    void copyAccessPoint(String esTyp) throws HurricanServiceCommandException {
        try {
            Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragDaten.getAuftragId(), esTyp);
            if (endstelle == null) {
                throw new FindException("Endstelle " + esTyp + " zum Auftrag " + auftragDaten.getAuftragId().toString()
                        + "konnte nicht ermittelt werden!");
            }

            // Endstellen-Adresse in Taifun ermitteln
            Adresse addressToCopy = billingAuftragService.findAnschlussAdresse4Auftrag(auftragDaten.getAuftragNoOrig(),
                    esTyp);

            if (addressToCopy != null) {
                // Adresse als Hurrican-Adress-Objekt speichern
                CCAddress address = kundenService.getCCAddress4BillingAddress(addressToCopy);
                address.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT);
                if (endstelle.getAddressId() != null) {
                    address.setId(endstelle.getAddressId());
                }
                kundenService.saveCCAddress(address);

                if (address.getId() == null) {
                    throw new HurricanServiceCommandException("Fehler beim Speichern der CC-Adresse");
                }

                // Ansprechpartner anlegen, wenn noch nicht vorhanden

                if (ansprechpartnerService.findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_B, auftragDaten.getAuftragId()) == null) {
                    EndstelleAnsprechpartnerView auftragAnsprechpartner = billingAuftragService.findEndstelleAnsprechpartner(auftragDaten.getAuftragNoOrig());
                    if (auftragAnsprechpartner != null && (auftragAnsprechpartner.getName() != null || StringUtils.isNotEmpty(auftragAnsprechpartner.getName()))) {
                        CCAddress adrAnsprechpartner = kundenService.getCCAddressAnsprechpartner(address, auftragAnsprechpartner);
                        kundenService.saveCCAddress(adrAnsprechpartner);

                        if (adrAnsprechpartner.getId() == null) {
                            throw new HurricanServiceCommandException("Fehler beim Speichern der AnsprechpartnerAdresse");
                        }
                        Ansprechpartner ansprechpartner = ansprechpartnerService.createPreferredAnsprechpartner(adrAnsprechpartner, auftragDaten.getAuftragId());
                        ansprechpartnerService.saveAnsprechpartner(ansprechpartner);

                        if (ansprechpartner.getId() == null) {
                            throw new HurricanServiceCommandException("Fehler beim Speichern des Ansprechpartners");
                        }
                    }
                }

                endstelle.setAddressId(address.getId());
                endstelle.setEndstelle(addressToCopy.getCombinedStreetData());
                endstelle.setName(addressToCopy.getCombinedNameData());
                endstelle.setOrt(addressToCopy.getCombinedOrtOrtsteil());
                endstelle.setPlz(addressToCopy.getPlzTrimmed());
                if (addressToCopy.getGeoId() != null) {
                    // GeoID wird in die Cache-Tabelle uebernommen, falls das noch nicht der Fall ist!
                    availabilityService.findOrCreateGeoId(addressToCopy.getGeoId(), sessionId);
                    endstelle.setGeoId(addressToCopy.getGeoId());
                }

                // Endstelle speichern
                endstellenService.saveEndstelle(endstelle);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim Abgleich der Standort-Adresse", e);
        }
    }

    /* Ueberprueft, ob alle benoetigten Daten vorhanden sind. */
    private void checkValues() throws ServiceCommandException, FindException {
        sessionId = getPreparedValue(KEY_SESSION_ID, Long.class, false, "Session-ID ist nicht angegeben!");
        setAuftragDaten((AuftragDaten) getPreparedValue(KEY_AUFTRAG_DATEN));
        if (auftragDaten == null) {
            throw new HurricanServiceCommandException(
                    "Es wurde kein Auftrag angegeben, auf den die Standortadresse kopiert werden soll!");
        }
    }

    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    public void setBillingAuftragService(BillingAuftragService billingAuftragService) {
        this.billingAuftragService = billingAuftragService;
    }

    public void setKundenService(CCKundenService kundenService) {
        this.kundenService = kundenService;
    }

    public void setAvailabilityService(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    public void setAnsprechpartnerService(AnsprechpartnerService ansprechpartnerService) {
        this.ansprechpartnerService = ansprechpartnerService;
    }

    public void setProdService(ProduktService prodService) {
        this.prodService = prodService;
    }

    public void setAuftragDaten(AuftragDaten auftragDaten) {
        this.auftragDaten = auftragDaten;
    }
}
