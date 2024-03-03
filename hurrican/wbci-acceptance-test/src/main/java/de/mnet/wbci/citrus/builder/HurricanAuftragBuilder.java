/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.14
 */
package de.mnet.wbci.citrus.builder;

import java.time.*;
import java.util.*;

import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.builder.hurrican.AbstractHurricanAuftragBuilder;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnik2Endstelle;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2Endstelle;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GeoIdCity;
import de.augustakom.hurrican.model.cc.GeoIdCountry;
import de.augustakom.hurrican.model.cc.GeoIdStreetSection;
import de.augustakom.hurrican.model.cc.GeoIdStreetSectionBuilder;
import de.augustakom.hurrican.model.cc.GeoIdZipCode;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;

/**
 *
 */
public class HurricanAuftragBuilder extends AbstractHurricanAuftragBuilder {

    public HurricanAuftragBuilder(CCAuftragDAO ccAuftragDAO) {
        super(ccAuftragDAO);
    }

    /**
     * @param billingOrderNoOrig               Billing-Auftrags Nummer
     * @param withDtagPortAndCarrierbestellung Flag, ob fuer den Auftrag eine Rangierung inkl. DTAG Port und
     *                                         Carrierbestellung angelegt werden soll
     * @return
     */
    public AuftragDaten builMaxiDslHvtAuftrag(
            Long billingOrderNoOrig,
            Long customerId,
            boolean withDtagPortAndCarrierbestellung,
            boolean withInvalidCarrierbestellung) {

        AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withKundeNo(customerId)
                .setPersist(false);
        Auftrag auftrag = ccAuftragDAO.store(auftragBuilder.build());

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_ANALOG)
                .withAuftragNoOrig(billingOrderNoOrig)
                .setPersist(false)
                .build();
        auftragDaten.setAuftragId(auftrag.getAuftragId());
        ccAuftragDAO.store(auftragDaten);

        AuftragTechnik2Endstelle at2Es = new AuftragTechnik2EndstelleBuilder().setPersist(false).build();
        ccAuftragDAO.store(at2Es);

        VerbindungsBezeichnung vbz = new VerbindungsBezeichnungBuilder()
                .withKindOfUseProduct("P")
                .withKindOfUseType("P")
                .withRandomUniqueCode()
                .setPersist(false).build();
        ccAuftragDAO.store(vbz);

        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .setPersist(false)
                .build();
        auftragTechnik.setAuftragId(auftrag.getAuftragId());
        auftragTechnik.setNiederlassungId(Niederlassung.ID_MUENCHEN);
        auftragTechnik.setAuftragTechnik2EndstelleId(at2Es.getId());
        auftragTechnik.setVbzId(vbz.getId());
        ccAuftragDAO.store(auftragTechnik);

        HVTGruppe hvtGruppe = new HVTGruppeBuilder().setPersist(false).build();
        ccAuftragDAO.store(hvtGruppe);

        HVTStandort hvtStandort = new HVTStandortBuilder()
                .withCarrierId(Carrier.ID_DTAG)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withCarrierKennungId(CarrierKennung.ID_MNET_MUENCHEN)
                .setPersist(false).build();
        hvtStandort.setHvtGruppeId(hvtGruppe.getId());
        ccAuftragDAO.store(hvtStandort);

        CCAddress address = new CCAddressBuilder()
                .setPersist(false).build();
        address.setHandy("+49 176 123456");
        address.setTelefon("+49 89 555222");
        ccAuftragDAO.store(address);

        Ansprechpartner ansprechpartner = new AnsprechpartnerBuilder()
                .withType(Ansprechpartner.Typ.ENDSTELLE_B)
                .setPersist(false).build();
        ansprechpartner.setAddress(address);
        ansprechpartner.setAuftragId(auftrag.getId());
        ccAuftragDAO.store(ansprechpartner);

        GeoIdStreetSection streetSection = new GeoIdStreetSectionBuilder().setPersist(false).build();
        GeoIdZipCode zipCode = streetSection.getZipCode();
        GeoIdCity city = zipCode.getCity();
        GeoIdCountry country = city.getCountry();

        ccAuftragDAO.store(country);
        ccAuftragDAO.store(city);
        ccAuftragDAO.store(zipCode);
        streetSection = ccAuftragDAO.store(streetSection);

        GeoId geoId = new GeoIdBuilder().setPersist(false).build();
        geoId.setStreetSection(streetSection);

        ccAuftragDAO.store(geoId);

        Endstelle endstelle = new EndstelleBuilder()
                .setPersist(false).build();
        endstelle.setEndstelleGruppeId(at2Es.getId());
        endstelle.setHvtIdStandort(hvtStandort.getId());
        endstelle.setAddressId(address.getId());
        endstelle.setGeoId(geoId.getId());
        ccAuftragDAO.store(endstelle);

        if (withDtagPortAndCarrierbestellung) {
            buildRangierungWithDtagPortAndCb(endstelle, withInvalidCarrierbestellung);
        }

        return auftragDaten;
    }


    private void buildRangierungWithDtagPortAndCb(Endstelle endstelle, boolean makeInvalid) {
        Equipment dtagPort = new EquipmentBuilder()
                .withDtagValues()
                .setPersist(false).build();
        dtagPort.setHvtIdStandort(endstelle.getHvtIdStandort());
        ccAuftragDAO.store(dtagPort);

        Rangierung rangierung = new RangierungBuilder()
                .withPhysikTypId(PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI)
                .setPersist(false).build();
        rangierung.setHvtIdStandort(endstelle.getHvtIdStandort());
        rangierung.setEqOutId(dtagPort.getId());
        rangierung.setEsId(-1L);
        rangierung.setFreigegeben(Rangierung.Freigegeben.freigegeben);
        ccAuftragDAO.store(rangierung);

        Carrierbestellung2Endstelle cb2Es = new Carrierbestellung2EndstelleBuilder()
                .setPersist(false).build();
        ccAuftragDAO.store(cb2Es);

        Carrierbestellung cb = new CarrierbestellungBuilder()
                .withLbz("96W/821/821/12345678")
                .withVtrNr("V123456")
                .withCarrier(Carrier.ID_DTAG)
                .setPersist(false).build();
        if (!makeInvalid) {
            cb.setBereitstellungAm(Date.from(LocalDateTime.now().minusDays(100).atZone(ZoneId.systemDefault()).toInstant()));
            cb.setZurueckAm(Date.from(LocalDateTime.now().minusDays(110).atZone(ZoneId.systemDefault()).toInstant()));
        }
        cb.setCb2EsId(cb2Es.getId());
        ccAuftragDAO.store(cb);

        endstelle.setRangierId(rangierung.getId());
        endstelle.setCb2EsId(cb2Es.getId());
        ccAuftragDAO.store(endstelle);
    }

}
