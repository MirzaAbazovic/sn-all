/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2007 10:00:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import static de.augustakom.hurrican.model.cc.VerbindungsBezeichnung.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragKombi;
import de.augustakom.hurrican.model.billing.BAuftragScv;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Command-Klasse, um  Report-Daten zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetAuftragDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetAuftragDatenCommand.class);

    public static final String KUNDENOORIG = "KundeNoOrig";
    public static final String AUFTRAGNOORIG = "AuftragNoOrig";
    public static final String AUFTRAGID = "AuftragId";
    public static final String BESTELLNR = "BestellNr";
    public static final String INBETRIEBNAHME = "Inbetriebnahme";
    public static final String BEMERKUNGEN = "Bemerkungen";
    public static final String KUENDIGUNG = "Kuendigung";
    public static final String ANGEBOTDATUM = "AngebotDatum";
    public static final String AUFTRAGDATUM = "AuftragDatum";
    public static final String VORGABEKUNDE = "VorgabeKunde";
    public static final String VORGABESCV = "VorgabeSCV";
    public static final String MMZ = "Mmz";
    public static final String VBZ = OLD_VBZ_NAME_CAMEL_CASE;
    public static final String PRODUKT = "Produkt";
    public static final String PRODUKTGRUPPE = "Produktgruppe";
    public static final String REALISIERUNGSTERMIN = "Realisierungstermin";
    public static final String ANSCHLUSSART = "Anschlussart";
    public static final String MVD = "Vertragslaufzeit";
    public static final String SAP_ID = "SapId";
    public static final String ZIELNUMMER_KUERZUNG = "ZielnummerKuerzung";
    public static final String SPEICHERUNG_VERBINDUNGSDATEN = "SpeicherungVerbindungsdaten";
    public static final String ENDSTELLE = "Endstelle";
    public static final String STRASSE = "EndstelleStrasse";
    public static final String ORT = "EndstelleOrt";
    public static final String KUNDE_VOR_ORT = "KundeVorOrt";
    public static final String BANDBREITE = "Bandbreite";
    public static final String BILLINGPRODUKTNAME = "BillingProduktName";
    public static final String OLDBILLINGPRODUKTNAME = "OldBillingProduktName";
    public static final String PORT_TIME = "Portierungszeitfenster";
    public static final String CB_BEREITSTELLUNG = "BereitstellungAm";
    public static final String BILLING_EINGANGSDATUM = "BillingEingangsdatum";
    public static final String BILLING_GUELTIG_VON = "BillingGueltigVon";
    public static final String BEARBEITER = "Bearbeiter";

    private Long auftragId = null;
    private Map<String, Object> map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<String, Object>();

            readAuftragDaten();
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
        return AUFTRAG_DATEN;
    }

    /* Ermittelt die Stammdaten eines Auftrags und schreibt diese in die HashMap. */
    protected void readAuftragDaten() throws HurricanServiceCommandException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            OEService oes = getBillingService(OEService.class);
            BillingAuftragService billingService = getBillingService(BillingAuftragService.class);

            Auftrag auftrag = as.findAuftragById(auftragId);
            map.put(getPropName(KUNDENOORIG), (auftrag != null) ? NumberTools.convertToString(auftrag.getKundeNo(), null) : null);

            // Ermittl Auftragdaten
            AuftragDaten ad = as.findAuftragDatenByAuftragId(auftragId);
            map.put(getPropName(AUFTRAGNOORIG), (ad != null) ? NumberTools.convertToString(ad.getAuftragNoOrig(), null) : null);
            map.put(getPropName(AUFTRAGID), NumberTools.convertToString(auftragId, null));
            map.put(getPropName(BESTELLNR), (ad != null) ? ad.getBestellNr() : null);
            map.put(getPropName(INBETRIEBNAHME), (ad != null) ? ad.getInbetriebnahme() : null);
            map.put(getPropName(BEMERKUNGEN), (ad != null) ? ad.getBemerkungen() : null);
            map.put(getPropName(KUENDIGUNG), (ad != null) ? ad.getKuendigung() : null);
            map.put(getPropName(ANGEBOTDATUM), (ad != null) ? ad.getAngebotDatum() : null);
            map.put(getPropName(AUFTRAGDATUM), (ad != null) ? ad.getAuftragDatum() : null);
            map.put(getPropName(VORGABEKUNDE), (ad != null) ? ad.getVorgabeKunde() : null);
            map.put(getPropName(VORGABESCV), (ad != null) ? ad.getVorgabeSCV() : null);
            map.put(getPropName(BEARBEITER), (ad != null) ? ad.getBearbeiter() : null);

            // Ermittle Produktname aus Billingsystem
            String billingProduktName = null;
            if ((ad != null) && (ad.getAuftragNoOrig() != null)) {
                billingProduktName = oes.findProduktName4Auftrag(ad.getAuftragNoOrig());
            }
            map.put(getPropName(BILLINGPRODUKTNAME), StringUtils.trimToEmpty(billingProduktName));

            // Ermittle Bandbreite
            String downstream = getStrValueOfActiveTechLeistung(auftragId, TechLeistung.TYP_DOWNSTREAM, "");
            String upstream = getStrValueOfActiveTechLeistung(auftragId, TechLeistung.TYP_UPSTREAM, "");
            map.put(getPropName(BANDBREITE), StringUtils.trimToEmpty(downstream) + "/"
                    + StringUtils.trimToEmpty(upstream) + " kbit/s");

            // MMZ
            Reference ref = null;
            if (ad != null) {
                ReferenceService rs = getCCService(ReferenceService.class);
                ref = rs.findReference(ad.getMmzId());
            }
            map.put(getPropName(MMZ), (ref != null) ? ref.getGuiText() : null);

            // VerbindungsBezeichnung
            PhysikService physikService = getCCService(PhysikService.class);
            VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragId(auftragId);
            map.put(getPropName(VBZ), (verbindungsBezeichnung != null) ? verbindungsBezeichnung.getVbz() : null);

            // Hurrican-Produktname
            ProduktService ps = getCCService(ProduktService.class);
            Produkt produkt = null;
            String prodName = null;
            if (ad != null) {
                produkt = ps.findProdukt(ad.getProdId());
                prodName = ps.generateProduktName4Auftrag(auftragId, produkt);
            }
            map.put(getPropName(PRODUKT), StringUtils.trimToEmpty(prodName));

            map.put(getPropName(ANSCHLUSSART),
                    (produkt != null) ? StringUtils.trimToNull(produkt.getAnschlussart()) : null);

            // Produktgruppe
            ProduktGruppe pg = null;
            if (produkt != null) {
                pg = ps.findProduktGruppe(produkt.getProduktGruppeId());
            }
            map.put(getPropName(PRODUKTGRUPPE), (pg != null) ? pg.getProduktGruppe() : null);

            // Realisierungstermin
            BAService bas = getCCService(BAService.class);
            Verlauf actVerlauf = bas.findActVerlauf4Auftrag(auftragId, false);
            Date realDate = (actVerlauf != null) ? actVerlauf.getRealisierungstermin() : null;
            map.put(getPropName(REALISIERUNGSTERMIN), realDate);

            // Vertragslaufzeit ermitteln
            BAuftrag bAuftrag = null;
            if ((ad != null) && (ad.getAuftragNoOrig() != null)) {
                bAuftrag = billingService.findAuftrag(ad.getAuftragNoOrig());
            }
            map.put(getPropName(MVD), (bAuftrag != null) ? bAuftrag.getVertragsLaufzeit() : null);
            map.put(getPropName(SAP_ID), (bAuftrag != null) ? bAuftrag.getSapId() : null);

            // Verbindungsdatenspeicherung ermitteln
            // Ermittle Auftragszusatz
            String auftragZusatz = null;
            BAuftragKombi bAuftragKombi = null;
            BAuftragScv bAuftragScv = null;
            if (bAuftrag != null) {
                OE oe = oes.findOE(bAuftrag.getOeNoOrig());
                auftragZusatz = (oe != null) ? oe.getAuftragZusatz() : null;
            }
            if (StringUtils.equals(auftragZusatz, OE.OE_AUFTRAGZUSATZ_KOMBI)) {
                if (bAuftrag != null) {
                    bAuftragKombi = billingService.findAuftragKombiByAuftragNo(bAuftrag.getAuftragNo());
                }
                map.put(getPropName(SPEICHERUNG_VERBINDUNGSDATEN), "ungekürzt speichern");
                map.put(getPropName(ZIELNUMMER_KUERZUNG), (bAuftragKombi != null) ? StringUtils.trimToEmpty(bAuftragKombi.getZielnummerKuerzung()) : null);
            }
            else if (StringUtils.equals(auftragZusatz, OE.OE_AUFTRAGZUSATZ_SCV)) {
                if (bAuftrag != null) {
                    bAuftragScv = billingService.findAuftragScvByAuftragNo(bAuftrag.getAuftragNo());
                }
                map.put(getPropName(SPEICHERUNG_VERBINDUNGSDATEN), "ungekürzt speichern");
                map.put(getPropName(ZIELNUMMER_KUERZUNG), (bAuftragScv != null) ? StringUtils.trimToEmpty(bAuftragScv.getZielnummerKuerzung()) : null);

            }
            else {
                map.put(getPropName(SPEICHERUNG_VERBINDUNGSDATEN), null);
                map.put(getPropName(ZIELNUMMER_KUERZUNG), null);
            }

            // Portierungszeitfenster ermitteln
            String portTime = null;
            if ((ad != null) && (ad.getAuftragNoOrig() != null)) {
                RufnummerService dnService = getBillingService(RufnummerService.class);
                List<Rufnummer> dns = dnService.findRNs4Auftrag(ad.getAuftragNoOrig());
                if (CollectionTools.isNotEmpty(dns)) {
                    for (Rufnummer dn : dns) {
                        // Eventl. Prüfung DN.RealDate == Auftrag.Realsierungsdatum
                        if (StringUtils.isEmpty(portTime) && (dn != null)
                                && (dn.getPortierungVon() != null)
                                && (dn.getPortierungBis() != null)) {
                            portTime = DateTools.getHourOfDay(dn.getPortierungVon()) + " bis "
                                    + DateTools.getHourOfDay(dn.getPortierungBis());
                        }
                    }
                }
            }
            map.put(getPropName(PORT_TIME), (portTime != null) ? portTime : null);

            // Ermittle Standort-Adresse des vorhergehenden Auftrags
            Adresse oldAuftragStandort = null;
            Long oldOrderNo = null;
            if (bAuftrag != null) {
                try {
                    oldOrderNo = NumberTools.convertString2Long(bAuftrag.getOldAuftragNoOrig());
                    oldAuftragStandort = (oldOrderNo != null) ? billingService.findAnschlussAdresse4Auftrag(oldOrderNo) : null;
                }
                catch (NumberFormatException e) {
                    LOGGER.warn("OldOrderNo konnte nicht konvertiert werden.");
                }
            }
            String strasse = null;
            String ort = null;
            String endstelle = null;
            if (oldAuftragStandort != null) {
                strasse = StringUtils.trimToEmpty(oldAuftragStandort.getStrasse())
                        + " " + StringUtils.trimToEmpty(oldAuftragStandort.getNummer())
                        + StringUtils.trimToEmpty(oldAuftragStandort.getHausnummerZusatz());
                ort = StringUtils.trimToEmpty(oldAuftragStandort.getPlz())
                        + " " + StringUtils.trimToEmpty(oldAuftragStandort.getCombinedOrtOrtsteil());
                endstelle = StringUtils.trimToEmpty(oldAuftragStandort.getFloor());
            }
            map.put(getPropName(ENDSTELLE), (endstelle != null) ? StringUtils.trimToEmpty(endstelle) : null);
            map.put(getPropName(STRASSE), (strasse != null) ? StringUtils.trimToEmpty(strasse) : null);
            map.put(getPropName(ORT), (ort != null) ? StringUtils.trimToEmpty(ort) : null);

            // Ermittle Kunde vor Ort
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle es = esSrv.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
            CarrierService cService = getCCService(CarrierService.class);
            Carrierbestellung cBestellung = null;
            if (es != null) {
                cBestellung = cService.findLastCB4Endstelle(es.getId());
            }
            map.put(getPropName(KUNDE_VOR_ORT), ((cBestellung != null) && (cBestellung.getKundeVorOrt() != null)) ? cBestellung.getKundeVorOrt().toString() : null);
            map.put(getPropName(CB_BEREITSTELLUNG), (cBestellung != null) ? cBestellung.getBereitstellungAm() : null);

            // Ermittle Produktname des vorhergehenden Auftrags
            String oldBillingProduktName = (oldOrderNo != null) ? oes.findProduktName4Auftrag(oldOrderNo) : null;
            map.put(getPropName(OLDBILLINGPRODUKTNAME), StringUtils.trimToEmpty(oldBillingProduktName));

            // Daten des aktuellen Billing-Auftrags ermitteln
            BAuftrag bAuftragAkt = null;
            if ((ad != null) && (ad.getAuftragNoOrig() != null)) {
                bAuftragAkt = billingService.findAuftragAkt(ad.getAuftragNoOrig());
            }
            map.put(getPropName(BILLING_EINGANGSDATUM), (bAuftragAkt != null) ? bAuftragAkt.getEingangsdatum() : null);
            map.put(getPropName(BILLING_GUELTIG_VON), (bAuftragAkt != null) ? bAuftragAkt.getGueltigVon() : null);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /*
     * Funktion liefert eine bestimmte techn. Leistung zu einem Auftrag
     */
    private String getStrValueOfActiveTechLeistung(Long auftragId, String techLsTyp, String defaultString) throws ServiceNotFoundException, FindException {
        CCLeistungsService ls =
                getCCService(CCLeistungsService.class);
        List<TechLeistung> techLeistungen =
                ls.findTechLeistungen4Auftrag(auftragId, techLsTyp, true);
        String value = null;
        if ((techLeistungen != null) && (techLeistungen.size() == 1)) {
            value = techLeistungen.get(0).getStrValue();
        }
        return (StringUtils.isNotBlank(value)) ? value : defaultString;
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("Auftrag-ID wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetAuftragDatenCommand.properties";
    }
}


