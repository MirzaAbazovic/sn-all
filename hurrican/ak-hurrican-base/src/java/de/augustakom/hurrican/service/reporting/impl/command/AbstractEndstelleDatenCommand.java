/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.01.2008 16:30:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse, um Daten zu einer best. Endstelle zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class AbstractEndstelleDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractEndstelleDatenCommand.class);

    public static final String ESID = "EsId";
    public static final String ESHVTSTDID = "EsHvtStdId";
    public static final String ESNAME = "EsName";
    public static final String ESENDSTELLE = "EsEndstelle";
    public static final String ESORT = "EsOrt";
    public static final String ESPLZ = "EsPlz";
    public static final String ESBEMSTAWA = "EsBemstawa";
    public static final String ESRANGIERID = "EsRangierId";
    public static final String ESANSP = "EsAnsprechpartner";
    public static final String ESSCHNITTSTELLE = "EsSchnittstelle";
    public static final String ESLEITUNGSART = "EsLeitungsart";
    public static final String ESANSCHLUSSART = "EsAnschlussart";
    public static final String ESADDRESS_ISSET = "EsAdresseIsSet";
    public static final String ESADDRESS = "EsAdresse";
    public static final String ESKVZNUMMER = "EsKvzNummer";
    public static final String ESKVZSCHALTNUMMER = "EsKvzSchaltnummer";

    public static final String EQINVERTEILER = "EqInVerteiler";
    public static final String EQINREIHE = "EqInReihe";
    public static final String EQINBUCHT = "EqInBucht";
    public static final String EQINLEISTE1 = "EqInLeiste1";
    public static final String EQINSTIFT1 = "EqInStift1";
    public static final String EQINLEISTE2 = "EqInLeiste2";
    public static final String EQINSTIFT2 = "EqInStift2";
    public static final String EQINUEVT = "EqInUetv";
    public static final String EQINRANGSCHNITTSTELLE = "EqInRangSchnittstelle";
    public static final String EQOUTVERTEILER = "EqOutVerteiler";
    public static final String EQOUTVERTEILERPUNKT = "EqOutVerteilerpunkt";
    public static final String EQOUTVERTEILERPUNKT2 = "EqOutVerteilerpunkt2";
    public static final String EQOUTREIHE = "EqOutReihe";
    public static final String EQOUTBUCHT = "EqOutBucht";
    public static final String EQOUTLEISTE1 = "EqOutLeiste1";
    public static final String EQOUTSTIFT1 = "EqOutStift1";
    // Der CU-Schaltauftrag benoetigt den kompletten Rang_Stift1
    public static final String EQOUTSTIFT1FULL = "EqOutStift1Full";
    public static final String EQOUTLEISTE2 = "EqOutLeiste2";
    public static final String EQOUTSTIFT2 = "EqOutStift2";
    public static final String EQOUTUEVT = "EqOutUetv";
    public static final String EQOUTRANGSCHNITTSTELLE = "EqOutRangSchnittstelle";
    public static final String EQZWEIDRAHT = "EqZweiDraht";
    public static final String EQVIERDRAHT = "EqVierDraht";


    public static final String CBLBZ = "CbLbz";
    public static final String CBLBZ2 = "CbLbz2";
    public static final String CBVTRNR = "CbVtrNr";
    public static final String CBBEREITSTELLUNG = "CbBereitstellung";
    public static final String CBAENDERUNGREALISIERUNG = "CbAenderungRealisierungstermin";
    public static final String CBKUENDIGUNGANCARRIER = "CbKuendigungAnCarrier";
    public static final String CBKUENDIGUNG = "CbKuendigungAm";
    public static final String CBCARRIERNAME = "CbCarrierName";
    public static final String CBVORGABEDATUM = "CbVorgabeDatum";
    public static final String AIADDRESS = "AIAdresse";

    public static final String NAME = "Name";
    public static final String VORNAME = "Vorname";
    public static final String NAME2 = "Name2";
    public static final String VORNAME2 = "Vorname2";
    public static final String STRASSE = "Strasse";
    public static final String STRASSE_ADD = "StrasseAdd";
    public static final String HAUSNUMMER = "Hausnummer";
    public static final String HAUSNUMMER_ZUSATZ = "HausnummerZusatz";
    public static final String PLZ = "PLZ";
    public static final String ORT = "Ort";
    public static final String ORT_ZUSATZ = "OrtZusatz";
    public static final String POSTFACH = "Postfach";


    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        return null;
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return ENDSTELLE_DATEN;
    }

    /**
     * Ermittelt die Daten zu einer best. Endstelle
     */
    protected Map<String, Object> readEndstelleDaten(Endstelle es, Long auftragId) throws HurricanServiceCommandException {
        try {
            EndstellenService endstellenService = getCCService(EndstellenService.class);
            PhysikService physikService = getCCService(PhysikService.class);
            CCKundenService kundenService = getCCService(CCKundenService.class);
            RangierungsService rangierungsService = getCCService(RangierungsService.class);
            AvailabilityService availabilityService = getCCService(AvailabilityService.class);
            HVTService hvtService = getCCService(HVTService.class);
            CCAuftragService auftragService = getCCService(CCAuftragService.class);

            Map<String, Object> map = new HashMap<>();

            // Ermittle Adress-Objekt der Endstelle
            AddressModel address = null;
            if (es != null) {
                address = endstellenService.findAnschlussadresse4Auftrag(auftragId, es.getEndstelleTyp());
                address = availabilityService.getDtagAddressForCb(es.getGeoId(), address);

            }

            // Daten der Hurrican-Endstelle
            map.put(getPropName(ESID), (es != null) ? es.getId() : null);
            map.put(getPropName(ESHVTSTDID), (es != null) ? es.getHvtIdStandort() : null);
            if (address != null) {
                map.put(getPropName(ESNAME), address.getCombinedNameData());
                // Strasse
                // Wie mit AM abgestimmt, wird nur Strasse inkl. Hausnummer verwendet.
                // Strassenzusatz wird nicht verwendet!
                GeoId geoId = null;
                if ((address instanceof Adresse) && (((Adresse) address).getGeoId() != null)) {
                    geoId = availabilityService.findGeoId(((Adresse) address).getGeoId());
                }
                String esStr = StringTools.join(
                        new String[] { ((geoId != null) ? geoId.getStreet() : address.getStrasse()),
                                address.getNummer() }, " ", true
                );
                esStr = StringTools.join(new String[] { esStr, address.getHausnummerZusatz() }, "", true);
                map.put(getPropName(ESENDSTELLE), esStr);

                // Ort
                map.put(getPropName(ESORT), (geoId != null) ? geoId.getCity() : address.getCombinedOrtOrtsteil());
                map.put(getPropName(ESPLZ), (geoId != null) ? geoId.getZipCode() : address.getPlzTrimmed());
            }
            else {
                map.put(getPropName(ESNAME), (es != null) ? es.getName() : null);
                map.put(getPropName(ESENDSTELLE), (es != null) ? es.getEndstelle() : null);
                map.put(getPropName(ESORT), (es != null) ? es.getOrt() : null);
                map.put(getPropName(ESPLZ), (es != null) ? es.getPlz() : null);
            }
            map.put(getPropName(ESBEMSTAWA), (es != null) ? es.getBemerkungStawa() : null);
            map.put(getPropName(ESRANGIERID), (es != null) ? es.getRangierId() : null);

            Ansprechpartner ansp = (es != null) ? getAnsp4ES(es, endstellenService) : null;
            map.put(getPropName(ESANSP), (ansp != null) ? ansp.getDisplayText() : null);
            Schnittstelle schnittstelle = (es != null) ? getSchnittstelle4ES(es.getId(), physikService) : null;
            map.put(getPropName(ESSCHNITTSTELLE), (schnittstelle != null) ? schnittstelle.getSchnittstelle() : null);
            Leitungsart lart = (es != null) ? getLeitungsart4ES(es.getId(), physikService) : null;
            map.put(getPropName(ESLEITUNGSART), (lart != null) ? lart.getName() : null);
            Anschlussart ansArt = (es != null) ? getAnschlussart(es.getAnschlussart()) : null;
            map.put(getPropName(ESANSCHLUSSART), (ansArt != null) ? ansArt.getAnschlussart() : null);

            // Carrierbestellung
            Carrierbestellung cb = (es != null) ? getCB4ES(es.getId()) : null;
            map.put(getPropName(CBLBZ), (cb != null) ? cb.getLbz() : null);
            map.put(getPropName(CBVTRNR), (cb != null) ? cb.getVtrNr() : null);
            map.put(getPropName(CBBEREITSTELLUNG), (cb != null) ? cb.getBereitstellungAm() : null);
            map.put(getPropName(CBKUENDIGUNGANCARRIER), (cb != null) ? cb.getKuendigungAnCarrier() : null);
            map.put(getPropName(CBKUENDIGUNG), (cb != null) ? cb.getKuendBestaetigungCarrier() : null);
            map.put(getPropName(CBVORGABEDATUM), (cb != null) ? cb.getVorgabedatum() : null);
            map.put(getPropName(CBCARRIERNAME), getCarrierName4CB(cb));

            // Adress-Objekt fuer Endstelle
            map.put(getPropName(ESADDRESS_ISSET), (address != null) ? Boolean.TRUE : Boolean.FALSE);
            map.put(getPropName(ESADDRESS), getMapFromAddress(address));

            // Ermittle Anschlussinhaber-Adress-Objekt
            map.put(getPropName(AIADDRESS), getMapFromAddress((cb != null) ? kundenService.findCCAddress(cb.getAiAddressId()) : null));

            // Ermittle Rangierungsdaten
            Rangierung rangierung = rangierungsService.findRangierung((es != null) ? es.getRangierId() : null);
            Equipment eqIn = rangierungsService.findEquipment((rangierung != null) ? rangierung.getEqInId() : null);
            map.put(getPropName(EQINVERTEILER), (eqIn != null) ? eqIn.getRangVerteiler() : null);
            map.put(getPropName(EQINREIHE), (eqIn != null) ? eqIn.getRangReihe() : null);
            map.put(getPropName(EQINBUCHT), (eqIn != null) ? eqIn.getRangBucht() : null);
            map.put(getPropName(EQINLEISTE1), (eqIn != null) ? eqIn.getRangLeiste1() : null);
            map.put(getPropName(EQINSTIFT1), (eqIn != null) ? StringUtils.right(eqIn.getRangStift1(), 2) : null);
            map.put(getPropName(EQINLEISTE2), (eqIn != null) ? eqIn.getRangLeiste2() : null);
            map.put(getPropName(EQINSTIFT2), (eqIn != null) ? StringUtils.right(eqIn.getRangStift2(), 2) : null);
            map.put(getPropName(EQINUEVT), ((eqIn != null) && (eqIn.getUetv() != null)) ? eqIn.getUetv().name() : null);
            map.put(getPropName(EQINRANGSCHNITTSTELLE), ((eqIn != null) && (eqIn.getRangSchnittstelle() != null)) ? eqIn.getRangSchnittstelle().name() : null);

            Equipment eqOut = rangierungsService.findEquipment((rangierung != null) ? rangierung.getEqOutId() : null);
            map.put(getPropName(EQOUTVERTEILER), (eqOut != null) ? eqOut.getRangVerteiler() : null);
            map.put(getPropName(EQOUTVERTEILERPUNKT), (eqOut != null) ? eqOut.getVerteilerpunktString() : null);
            map.put(getPropName(EQOUTREIHE), (eqOut != null) ? eqOut.getRangReihe() : null);
            map.put(getPropName(EQOUTBUCHT), (eqOut != null) ? eqOut.getRangBucht() : null);
            map.put(getPropName(EQOUTLEISTE1), (eqOut != null) ? eqOut.getRangLeiste1() : null);
            map.put(getPropName(EQOUTSTIFT1), (eqOut != null) ? StringUtils.right(eqOut.getRangStift1(), 2) : null);
            map.put(getPropName(EQOUTSTIFT1FULL), (eqOut != null) ? eqOut.getRangStift1() : null);
            map.put(getPropName(EQOUTLEISTE2), (eqOut != null) ? eqOut.getRangLeiste2() : null);
            map.put(getPropName(EQOUTSTIFT2), (eqOut != null) ? StringUtils.right(eqOut.getRangStift2(), 2) : null);
            map.put(getPropName(EQOUTUEVT), ((eqOut != null) && (eqOut.getUetv() != null)) ? eqOut.getUetv().name() : null);
            map.put(getPropName(EQOUTRANGSCHNITTSTELLE), ((eqOut != null) && (eqOut.getRangSchnittstelle() != null)) ? eqOut.getRangSchnittstelle().name() : null);

            if (eqOut != null) {
                // Angabe von KVZ Daten!
                UEVT uevt = hvtService.findUEVT(eqOut.getHvtIdStandort(), eqOut.getRangVerteiler());
                map.put(getPropName(ESKVZNUMMER), eqOut.getKvzNummer());
                map.put(getPropName(ESKVZSCHALTNUMMER), uevt.getUevt());

                if (StringUtils.isNotBlank(eqOut.getKvzDoppelader())) {
                    // Leiste + Stift durch Werte aus KVZ Doppelader ueberschreiben!
                    map.put(getPropName(EQOUTLEISTE1), StringUtils.substring(eqOut.getKvzDoppelader(), 0, 2));
                    map.put(getPropName(EQOUTSTIFT1), StringUtils.substring(eqOut.getKvzDoppelader(), 2, 4));
                }
            }

            // Prüfe Zwei-Draht - Vier-Draht
            if (auftragService.has4DrahtOption(auftragId)) {
                map.put(getPropName(EQVIERDRAHT), Boolean.TRUE);
                map.put(getPropName(EQZWEIDRAHT), Boolean.FALSE);
                fillDataWithCorrespondingAuftrag(es, auftragId, map);
            }
            else {
                map.put(getPropName(EQVIERDRAHT), Boolean.FALSE);
                map.put(getPropName(EQZWEIDRAHT), Boolean.TRUE);
                map.put(getPropName(CBLBZ2), null);
                map.put(getPropName(EQOUTVERTEILERPUNKT2), null);
            }

            return map;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    private void fillDataWithCorrespondingAuftrag(Endstelle es, Long auftragId, Map<String, Object> map) throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        Carrierbestellung cb2 = findOtherCarrierBestellungFor4DrahtAuftrag(es, auftragId);
        map.put(getPropName(CBLBZ2), (cb2 != null) ? cb2.getLbz() : null);
        Equipment e = findOtherEquipmentFor4DrahtAuftrag(es, auftragId);
        map.put(getPropName(EQOUTVERTEILERPUNKT2), (e != null) ? e.getVerteilerpunktString() : null);
        map.put(getPropName(EQOUTLEISTE2), (e != null) ? e.getRangLeiste1() : null);
        map.put(getPropName(EQOUTSTIFT2), (e != null) ? StringUtils.right(e.getRangStift1(), 2) : null);
    }

    private Equipment findOtherEquipmentFor4DrahtAuftrag(Endstelle es, Long auftragId) throws ServiceNotFoundException, FindException {
        EndstellenService endstellenService = getCCService(EndstellenService.class);
        AuftragDaten matching = findAuftragDatenOfAssociatedAuftrag(auftragId);
        if (matching == null) {
            return null;
        }

        List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(matching.getAuftragId());
        for (Endstelle ess : endstellen) {
            if (ess.getEndstelleTyp().equals(es.getEndstelleTyp())) {
                RangierungsService rs = getCCService(RangierungsService.class);
                Rangierung rangB = rs.findRangierung(ess.getRangierId());
                if (rangB == null) {
                    return null;
                }
                return rs.findEquipment(rangB.getEqOutId());
            }
        }
        return null;
    }

    private Carrierbestellung findOtherCarrierBestellungFor4DrahtAuftrag(Endstelle es, Long auftragId) throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        EndstellenService endstellenService = getCCService(EndstellenService.class);
        AuftragDaten matching = findAuftragDatenOfAssociatedAuftrag(auftragId);
        if (matching == null) {
            return null;
        }
        List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(matching.getAuftragId());
        for (Endstelle ess : endstellen) {
            if (ess.getEndstelleTyp().equals(es.getEndstelleTyp())) {
                return getCB4ES(ess.getId());
            }
        }
        return null;
    }

    /**
     * Sucht den zugehoerigen 4Draht oder Nicht-4Draht-Auftrag.
     */
    private AuftragDaten findAuftragDatenOfAssociatedAuftrag(Long auftragId) throws FindException, ServiceNotFoundException {
        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        ProduktService ps = getCCService(ProduktService.class);
        AuftragDaten ad = auftragService.findAuftragDatenByAuftragId(auftragId);
        Long auftragNoOrig = ad.getAuftragNoOrig();
        List<AuftragDaten> auftragDaten = auftragService.findAuftragDaten4OrderNoOrig(auftragNoOrig);
        if (BooleanTools.nullToFalse(ps.isVierDrahtProdukt(ad.getProdId()))) {
            for (AuftragDaten auftragD : auftragDaten) {
                if (!BooleanTools.nullToFalse(ps.isVierDrahtProdukt(auftragD.getProdId()))) {
                    return auftragD;
                }
            }
        }
        else {
            for (AuftragDaten auftragD : auftragDaten) {
                if (BooleanTools.nullToFalse(ps.isVierDrahtProdukt(auftragD.getProdId()))) {
                    return auftragD;
                }
            }
        }
        return null;
    }

    /* Ermittelt den Ansprechpartner fuer eine best. Endstelle. */
    private Ansprechpartner getAnsp4ES(Endstelle es, EndstellenService esSrv) throws HurricanServiceCommandException {
        try {
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(es.getId());

            if (auftragDaten != null) {
                Ansprechpartner.Typ ansprechpartnerTyp = (es.isEndstelleB()) ? ENDSTELLE_B : ENDSTELLE_A;
                return getCCService(AnsprechpartnerService.class).findPreferredAnsprechpartner(ansprechpartnerTyp, auftragDaten.getAuftragId());
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ermittelt die Schnittstelle einer best. Endstelle. */
    private Schnittstelle getSchnittstelle4ES(Long esId, PhysikService ps) throws HurricanServiceCommandException {
        try {
            return ps.findSchnittstelle4ES(esId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ermittelt Name des Carriers zu einer Carrierbestellung. */
    private String getCarrierName4CB(Carrierbestellung cb) throws HurricanServiceCommandException {
        if (cb == null) { return null; }
        try {
            CarrierService cs = getCCService(CarrierService.class);
            Carrier carrier = cs.findCarrier(cb.getCarrier());

            return (carrier != null) ? carrier.getName() : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ermittelt die Leitungsart einer best. Endstelle. */
    private Leitungsart getLeitungsart4ES(Long esId, PhysikService ps) throws HurricanServiceCommandException {
        try {
            return ps.findLeitungsart4ES(esId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ermittelt die Anschlussart zu einer best. ID. */
    private Anschlussart getAnschlussart(Long ansArtId) throws HurricanServiceCommandException {
        try {
            PhysikService ps = getCCService(PhysikService.class);
            return ps.findAnschlussart(ansArtId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ermittelt die letzte Carrierbestellung zu einer best. Endstelle. */
    private Carrierbestellung getCB4ES(Long esId) throws HurricanServiceCommandException {
        try {
            CarrierService cs = getCCService(CarrierService.class);
            List<Carrierbestellung> cbs = cs.findCBs4Endstelle(esId);
            return ((cbs != null) && (!cbs.isEmpty())) ? cbs.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Erzeugt aus Adress-Objekt eine HashMap */
    private Map<String, Object> getMapFromAddress(AddressModel address) {
        Map<String, Object> map = new HashMap<>();
        map.put(NAME, (address != null) ? address.getName() : null);
        map.put(VORNAME, (address != null) ? address.getVorname() : null);
        map.put(NAME2, (address != null) ? address.getName2() : null);
        map.put(VORNAME2, (address != null) ? address.getVorname2() : null);
        map.put(STRASSE, (address != null) ? address.getStrasse() : null);
        map.put(STRASSE_ADD, (address != null) ? address.getStrasseAdd() : null);
        map.put(HAUSNUMMER, (address != null) ? address.getNummer() : null);
        map.put(HAUSNUMMER_ZUSATZ, (address != null) ? address.getHausnummerZusatz() : null);
        map.put(PLZ, (address != null) ? address.getPlz() : null);
        map.put(ORT, (address != null) ? address.getOrt() : null);
        map.put(ORT_ZUSATZ, (address != null) ? address.getOrtsteil() : null);
        map.put(POSTFACH, (address != null) ? address.getPostfach() : null);
        return map;
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetEndstelleDatenCommand.properties";
    }
}


