/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2007 15:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Command-Klasse, um Endstellen-Daten zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetEndstellenDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetEndstellenDatenCommand.class);

    public static final String TAL_ID_A = "TAL_ID_A";
    public static final String ESIDA = "EsIdA";
    public static final String ESHVTSTDIDA = "EsHvtStdIdA";
    public static final String ESNAMEA = "EsNameA";
    public static final String ESENDSTELLEA = "EsEndstelleA";
    public static final String ESORTA = "EsOrtA";
    public static final String ESPLZA = "EsPlzA";
    public static final String ESBEMSTAWAA = "EsBemstawaA";
    public static final String ESRANGIERIDA = "EsRangierIdA";
    public static final String ESANSPA = "EsAnsprechpartnerA";
    public static final String ESSCHNITTSTELLEA = "EsSchnittstelleA";
    public static final String ESLEITUNGSARTA = "EsLeitungsartA";
    public static final String ESANSCHLUSSARTA = "EsAnschlussartA";
    public static final String ESADDRESSA_ISSET = "EsAdresseAIsSet";
    public static final String ESADDRESSA = "EsAdresseA";

    public static final String CBLBZA = "CbLbzA";
    public static final String CBVTRNRA = "CbVtrNrA";
    public static final String CBBEREITSTELLUNGA = "CbBereitstellungA";
    public static final String CBAENDERUNGREALISIERUNGA = "CbAenderungRealisierungsterminA";
    public static final String CBKUENDIGUNGANCARRIERA = "CbKuendigungAnCarrierA";
    public static final String CBKUENDIGUNGA = "CbKuendigungAmA";
    public static final String AIADDRESSA = "AIAdresseA";

    public static final String TAL_ID_B = "TAL_ID_B";
    public static final String ESIDB = "EsIdB";
    public static final String ESHVTSTDIDB = "EsHvtStdIdB";
    public static final String ESNAMEB = "EsNameB";
    public static final String ESENDSTELLEB = "EsEndstelleB";
    public static final String ESORTB = "EsOrtB";
    public static final String ESPLZB = "EsPlzB";
    public static final String ESBEMSTAWAB = "EsBemstawaB";
    public static final String ESRANGIERIDB = "EsRangierIdB";
    public static final String ESANSPB = "EsAnsprechpartnerB";
    public static final String ESSCHNITTSTELLEB = "EsSchnittstelleB";
    public static final String ESLEITUNGSARTB = "EsLeitungsartB";
    public static final String ESANSCHLUSSARTB = "EsAnschlussartB";
    public static final String ESADDRESSB_ISSET = "EsAdresseBIsSet";
    public static final String ESADDRESSB = "EsAdresseB";

    public static final String CBLBZB = "CbLbzB";
    public static final String CBVTRNRB = "CbVtrNrB";
    public static final String CBBEREITSTELLUNGB = "CbBereitstellungB";
    public static final String CBAENDERUNGREALISIERUNGB = "CbAenderungRealisierungsterminB";
    public static final String CBKUENDIGUNGANCARRIERB = "CbKuendigungAnCarrierB";
    public static final String CBKUENDIGUNGB = "CbKuendigungAmB";
    public static final String AIADDRESSB = "AIAdresseB";

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

    private Long auftragId = null;
    private Long auftragNoOrig = null;

    private Map<String, Object> map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<>();

            readEndstellenDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return ENDSTELLEN_DATEN;
    }

    /**
     * Ermittelt die Endstellen-Daten zu einem best. Auftrag
     */
    private void readEndstellenDaten() throws HurricanServiceCommandException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            PhysikService physikService = getCCService(PhysikService.class);
            CCKundenService kundenService = getCCService(CCKundenService.class);

            List<Endstelle> endstellen = esSrv.findEndstellen4Auftrag(auftragId);
            if (endstellen != null) {
                for (Endstelle es : endstellen) {
                    if (es.isEndstelleA()) {
                        map.put(getPropName(ESIDA), es.getId());
                        map.put(getPropName(ESHVTSTDIDA), es.getHvtIdStandort());
                        map.put(getPropName(ESNAMEA), es.getName());
                        map.put(getPropName(ESENDSTELLEA), es.getEndstelle());
                        map.put(getPropName(ESORTA), es.getOrt());
                        map.put(getPropName(ESPLZA), es.getPlz());
                        map.put(getPropName(ESBEMSTAWAA), es.getBemerkungStawa());
                        map.put(getPropName(ESRANGIERIDA), es.getRangierId());
                        map.put(getPropName(TAL_ID_A), getTalID(auftragId, es.getEndstelleTyp(), physikService));

                        EndstelleAnsprechpartner esAnsp = getEsAnsp4ES(es.getId(), esSrv);
                        map.put(getPropName(ESANSPA), (esAnsp != null) ? esAnsp.getAnsprechpartner() : null);
                        Schnittstelle schnittstelle = getSchnittstelle4ES(es.getId(), physikService);
                        map.put(getPropName(ESSCHNITTSTELLEA), (schnittstelle != null) ? schnittstelle.getSchnittstelle() : null);
                        Leitungsart lart = getLeitungsart4ES(es.getId(), physikService);
                        map.put(getPropName(ESLEITUNGSARTA), (lart != null) ? lart.getName() : null);
                        Anschlussart ansArt = getAnschlussart(es.getAnschlussart());
                        map.put(getPropName(ESANSCHLUSSARTA), (ansArt != null) ? ansArt.getAnschlussart() : null);

                        // Carrierbestellung
                        Carrierbestellung cb = getCB4ES(es.getId());
                        map.put(getPropName(CBLBZA), (cb != null) ? cb.getLbz() : null);
                        map.put(getPropName(CBVTRNRA), (cb != null) ? cb.getVtrNr() : null);
                        map.put(getPropName(CBBEREITSTELLUNGA), (cb != null) ? cb.getBereitstellungAm() : null);
                        map.put(getPropName(CBKUENDIGUNGANCARRIERA), (cb != null) ? cb.getKuendigungAnCarrier() : null);
                        map.put(getPropName(CBKUENDIGUNGA), (cb != null) ? cb.getKuendBestaetigungCarrier() : null);

                        // Ermittle Adress-Objekt
                        AddressModel address = esSrv.findAnschlussadresse4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_A);
                        map.put(getPropName(ESADDRESSA_ISSET), (address != null) ? Boolean.TRUE : Boolean.FALSE);
                        map.put(getPropName(ESADDRESSA), getMapFromAddress(address));

                        // Ermittle Anschlussinhaber-Adress-Objekt
                        map.put(getPropName(AIADDRESSA), (cb != null) ? getMapFromAddress(kundenService.findCCAddress(cb.getAiAddressId())) : null);
                    }
                    else if (es.isEndstelleB()) {
                        map.put(getPropName(ESIDB), es.getId());
                        map.put(getPropName(ESHVTSTDIDB), es.getHvtIdStandort());
                        map.put(getPropName(ESNAMEB), es.getName());
                        map.put(getPropName(ESENDSTELLEB), es.getEndstelle());
                        map.put(getPropName(ESORTB), es.getOrt());
                        map.put(getPropName(ESPLZB), es.getPlz());
                        map.put(getPropName(ESBEMSTAWAB), es.getBemerkungStawa());
                        map.put(getPropName(ESRANGIERIDB), es.getRangierId());
                        map.put(getPropName(TAL_ID_B), getTalID(auftragId, es.getEndstelleTyp(), physikService));

                        EndstelleAnsprechpartner esAnsp = getEsAnsp4ES(es.getId(), esSrv);
                        map.put(getPropName(ESANSPB), (esAnsp != null) ? esAnsp.getAnsprechpartner() : null);
                        Schnittstelle schnittstelle = getSchnittstelle4ES(es.getId(), physikService);
                        map.put(getPropName(ESSCHNITTSTELLEB), (schnittstelle != null) ? schnittstelle.getSchnittstelle() : null);
                        Leitungsart lart = getLeitungsart4ES(es.getId(), physikService);
                        map.put(getPropName(ESLEITUNGSARTB), (lart != null) ? lart.getName() : null);
                        Anschlussart ansArt = getAnschlussart(es.getAnschlussart());
                        map.put(getPropName(ESANSCHLUSSARTB), (ansArt != null) ? ansArt.getAnschlussart() : null);

                        // Carrierbestellung
                        Carrierbestellung cb = getCB4ES(es.getId());
                        map.put(getPropName(CBLBZB), (cb != null) ? cb.getLbz() : null);
                        map.put(getPropName(CBVTRNRB), (cb != null) ? cb.getVtrNr() : null);
                        map.put(getPropName(CBBEREITSTELLUNGB), (cb != null) ? cb.getBereitstellungAm() : null);
                        map.put(getPropName(CBKUENDIGUNGANCARRIERB), (cb != null) ? cb.getKuendigungAnCarrier() : null);
                        map.put(getPropName(CBKUENDIGUNGB), (cb != null) ? cb.getKuendBestaetigungCarrier() : null);

                        // Ermittle Adress-Objekt
                        AddressModel address = esSrv.findAnschlussadresse4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
                        map.put(getPropName(ESADDRESSB_ISSET), (address != null) ? Boolean.TRUE : Boolean.FALSE);
                        map.put(getPropName(ESADDRESSB), getMapFromAddress(address));

                        // Ermittle Anschlussinhaber-Adress-Objekt
                        map.put(getPropName(AIADDRESSB), (cb != null) ? getMapFromAddress(kundenService.findCCAddress(cb.getAiAddressId())) : null);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ermittelt die TAL-ID fuer die angegebene Endstelle */
    private String getTalID(Long auftragId, String esTyp, PhysikService physikService) throws HurricanServiceCommandException {
        try {
            return physikService.getVbzValue4TAL(auftragId, esTyp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ermittelt den Ansprechpartner fuer eine best. Endstelle. */
    private EndstelleAnsprechpartner getEsAnsp4ES(Long esId, EndstellenService esSrv) throws HurricanServiceCommandException {
        try {
            return esSrv.findESAnsp4ES(esId);
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

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("AuftragId wurde dem Command-Objekt nicht uebergeben!");
        }
        tmpId = getPreparedValue(ORDER_NO_ORIG);
        auftragNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragNoOrig == null) {
            throw new HurricanServiceCommandException("AuftragNoOrig wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /* Erzeugt aus Adress-Objekt eine HashMap */
    private Map<String, String> getMapFromAddress(AddressModel address) {
        Map<String, String> map = new HashMap<>();
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
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetEndstellenDatenCommand.properties";
    }

}


