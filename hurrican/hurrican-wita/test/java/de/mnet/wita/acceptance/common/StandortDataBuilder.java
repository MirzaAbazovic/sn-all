/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2011 08:43:31
 */
package de.mnet.wita.acceptance.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.GeoIdDAO;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.GeoIdCity;
import de.augustakom.hurrican.model.cc.GeoIdCountry;
import de.augustakom.hurrican.model.cc.GeoIdDistrict;
import de.augustakom.hurrican.model.cc.GeoIdStreetSection;
import de.augustakom.hurrican.model.cc.GeoIdZipCode;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.HVTService;

public abstract class StandortDataBuilder<BUILDER extends StandortDataBuilder<BUILDER>> {

    private StandortData standortData;

    private String onkz = "8433";
    private int asb = 1;
    private Long carrierKennungId = CarrierKennung.ID_MNET_MUENCHEN;

    private String plz = "86571";
    private String ort = "Langenmosen";
    private String ortsteil = "Ortsteil";

    private String hvtStrasse = "Amselweg";
    private String hvtHausNr = "1";

    private String kundeStrasse = "Columbusstr.";
    private String kundeHausNr = "8";
    private String kundeHausNrZusatz = null;

    private String uevt = "0001";

    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private HVTService hvtService;

    @Qualifier("txGeoIdDAO")
    @Autowired
    private GeoIdDAO geoIdDAO;

    public StandortData build() throws StoreException {
        HVTGruppeBuilder hvtGruppeBuilder = (new HVTGruppeBuilder()).withStrasse(hvtStrasse).withHausNr(hvtHausNr)
                .withOnkz(onkz).withPlz(plz).withOrt(ort);
        HVTStandortBuilder hvtStandortBuilder = (new HVTStandortBuilder()).withHvtGruppeBuilder(hvtGruppeBuilder)
                .withCarrierKennungId(carrierKennungId).withAsb(asb)
                .withCarrierId(Carrier.ID_DTAG)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT);

        // Achtung: Standortadresse fuer StandortA wird aus Taifun ermittelt
        // (bei gewuenschter Aenderung des Kundenstandorts muss also auch das SQL-File angepasst werden!)
        GeoIdBuilder geoIdBuilder = (new GeoIdBuilder()).withZipCode(plz).withCity(ort).withDistrict(ortsteil)
                .withStreet(kundeStrasse).withHouseNum(kundeHausNr).withHouseNumExtension(kundeHausNrZusatz);

        GeoId2TechLocationBuilder geoId2TechLocationBuilder = (new GeoId2TechLocationBuilder()).withHvtStandortBuilder(
                hvtStandortBuilder).withGeoIdBuilder(geoIdBuilder);

        HVTGruppe hvtGruppe = geoId2TechLocationBuilder.getHvtStandortBuilder().getHvtGruppeBuilder().setPersist(false)
                .get();
        hvtGruppe = hvtService.saveHVTGruppe(hvtGruppe);

        HVTStandort hvtStandort = geoId2TechLocationBuilder.getHvtStandortBuilder().setPersist(false).get();
        hvtStandort.setHvtGruppeId(hvtGruppe.getId());
        hvtStandort = hvtService.saveHVTStandort(hvtStandort);

        GeoId geoId = geoId2TechLocationBuilder.getGeoIdBuilder().setPersist(false).get();
        GeoIdStreetSection geoIdStreetSection = geoId.getStreetSection();
        GeoIdZipCode geoIdZipCode = geoIdStreetSection.getZipCode();
        GeoIdDistrict geoIdDistrict = geoIdStreetSection.getDistrict();
        GeoIdCity geoIdCity = geoIdZipCode.getCity();
        GeoIdCountry geoIdCountry = geoIdCity.getCountry();
        geoIdCity.setCountry(geoIdDAO.save(geoIdCountry));
        geoIdZipCode.setCity(geoIdDAO.save(geoIdCity));
        geoIdStreetSection.setZipCode(geoIdDAO.save(geoIdZipCode));
        if (geoIdDistrict != null) {
            geoIdStreetSection.setDistrict(geoIdDAO.save(geoIdDistrict));
        }
        geoId.setStreetSection(geoIdDAO.save(geoIdStreetSection));
        geoIdDAO.save(geoId);
        // availabilityService.saveGeoId(geoId);

        GeoId2TechLocation geoId2TechLocation = geoId2TechLocationBuilder.setPersist(false).get();
        geoId2TechLocation.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        geoId2TechLocation.setGeoId(geoId.getId());
        // use random sessionId due to
        // DefaultHurricanService.getUserNameAndFirstNameSilent(Long)
        availabilityService.saveGeoId2TechLocation(geoId2TechLocation, Long.valueOf(42));

        standortData = new StandortData(hvtGruppe, hvtStandort, geoId, geoId2TechLocation, uevt);
        return standortData;
    }

    public StandortData get() throws StoreException {
        if (standortData == null) {
            build();
        }
        return standortData;
    }

    protected BUILDER withOnkz(String onkz) {
        this.onkz = onkz;
        return getThis();
    }

    protected BUILDER withAsb(int asb) {
        this.asb = asb;
        return getThis();
    }

    protected BUILDER withCarrierKennungId(Long carrierKennungId) {
        this.carrierKennungId = carrierKennungId;
        return getThis();
    }

    protected BUILDER withPlz(String plz) {
        this.plz = plz;
        return getThis();
    }

    protected BUILDER withOrt(String ort) {
        this.ort = ort;
        return getThis();
    }

    protected BUILDER withHvtStrasse(String hvtStrasse) {
        this.hvtStrasse = hvtStrasse;
        return getThis();
    }

    protected BUILDER withHvtHausNr(String hvtHausNr) {
        this.hvtHausNr = hvtHausNr;
        return getThis();
    }

    /**
     * <b>Achtung:</b> Standortadresse fuer StandortA wird in der Regel aus Taifun ermittelt (bei gewuenschter Aenderung
     * des Kundenstandorts muss also auch das SQL-File angepasst werden!)
     */
    protected BUILDER withKundeStrasse(String kundeStrasse) {
        this.kundeStrasse = kundeStrasse;
        return getThis();
    }

    /**
     * <b>Achtung:</b> Standortadresse fuer StandortA wird in der Regel aus Taifun ermittelt (bei gewuenschter Aenderung
     * des Kundenstandorts muss also auch das SQL-File angepasst werden!)
     */
    protected BUILDER withKundeHausNr(String kundeHausNr) {
        this.kundeHausNr = kundeHausNr;
        return getThis();
    }

    protected BUILDER withKundeHausNrZusatz(String kundeHausNrZusatz) {
        this.kundeHausNrZusatz = kundeHausNrZusatz;
        return getThis();
    }

    protected BUILDER withOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
        return getThis();
    }

    protected BUILDER withUevt(String uevt) {
        this.uevt = uevt;
        return getThis();
    }

    private BUILDER getThis() {
        @SuppressWarnings("unchecked")
        BUILDER result = (BUILDER) this;
        return result;
    }
}
