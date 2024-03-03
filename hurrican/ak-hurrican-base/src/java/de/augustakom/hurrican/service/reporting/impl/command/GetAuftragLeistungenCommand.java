/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2007 10:00:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.text.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.LeistungService;


/**
 * Command-Klasse, um  Report-Daten zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetAuftragLeistungenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetAuftragLeistungenCommand.class);

    public static final String LEISTUNGEN = "Leistungen";
    public static final String LEISTUNGEN_AKT = "LeistungenAKT";
    public static final String NAME = "Name";
    public static final String MENGE = "Menge";
    public static final String PREIS_BRUTTO = "PreisBrutto";
    public static final String PREIS_NETTO = "PreisNetto";
    public static final String STARTDATUM = "StartDatum";
    public static final String ENDDATUM = "EndDatum";
    public static final String KATEGORIE = "Kategorie";

    private static final String PREIS_QUELLE_AUFTRAGPOS = "service_element";
    private static final String PREIS_QUELLE_LEISTUNG = "price_element";


    private Long auftragNoOrig = null;
    private Map map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap();

            readAuftragLeistungen();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    public String getPrefix() {
        return AUFTRAG_LEISTUNGEN;
    }

    /* Ermittelt die Stammdaten eines Auftrags und schreibt diese in die HashMap. */
    protected void readAuftragLeistungen() throws HurricanServiceCommandException {
        try {
            BillingAuftragService bs = (BillingAuftragService) getBillingService(BillingAuftragService.class);
            // Ermittle alle Leistungen
            List<BAuftragPos> auftragPos = bs.findBAuftragPos4Report(auftragNoOrig, false);
            addLeistungen(auftragPos, LEISTUNGEN);
            // Ermittle nur leztzte Änderung der Leistungen
            auftragPos = bs.findBAuftragPos4Report(auftragNoOrig, true);
            addLeistungen(auftragPos, LEISTUNGEN_AKT);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /*
     * Fügt Leistungen der HashMap hinzu
     */
    private void addLeistungen(List<BAuftragPos> auftragPos, String hashKey) throws Exception {
        if (CollectionTools.isNotEmpty(auftragPos)) {
            LeistungService ls = (LeistungService) getBillingService(LeistungService.class);
            List list = new ArrayList();

            // Durchlaufe alle Auftragpositionen
            for (BAuftragPos pos : auftragPos) {
                if (pos != null) {
                    Map map2 = new HashMap();
                    map2.put(MENGE, pos.getMenge());
                    map2.put(STARTDATUM, pos.getChargeFrom());
                    map2.put(ENDDATUM, pos.getChargeTo());

                    Leistung leistung = ls.findLeistung(pos.getLeistungNoOrig());

                    // Rechnungstext
                    String reText = ls.findRechnungstext(pos.getLeistungNoOrig(), pos.getParameter(), null);
                    // Verwende alternativ den Leistungsnamen
                    if (StringUtils.isBlank(reText)) {
                        reText = leistung.getName();
                    }
                    map2.put(NAME, (reText != null) ? StringUtils.trimToEmpty(reText) : null);

                    // Preis und Kategorie ermitteln
                    Float preis = null;
                    Float preis_brutto = null;
                    String kat = null;

                    if (leistung != null) {
                        if (StringUtils.equals(leistung.getPreisQuelle(), PREIS_QUELLE_AUFTRAGPOS)) {
                            preis = pos.getDefinedPrice();
                        }
                        else if (StringUtils.equals(leistung.getPreisQuelle(), PREIS_QUELLE_LEISTUNG)) {
                            preis = leistung.getPreis();
                        }
                        // Brutto-Preis ermitteln
                        if (preis != null && leistung.getVatCode() != null) {
                            Float mwst = ls.findVatRate(leistung.getVatCode());
                            if (mwst != null) {
                                preis_brutto = (preis * mwst / 100) + preis;
                            }
                        }
                        kat = leistung.getLeistungKat();
                    }

                    DecimalFormat df = new DecimalFormat("#0.00");
                    map2.put(PREIS_NETTO, (preis != null) ? df.format(preis) : null);
                    map2.put(PREIS_BRUTTO, (preis_brutto != null) ? df.format(preis_brutto) : null);
                    map2.put(KATEGORIE, (kat != null) ? StringUtils.trimToEmpty(kat) : null);

                    list.add(map2);
                }
            }
            map.put(getPropName(hashKey), list);
        }
        else {
            map.put(getPropName(hashKey), new ArrayList());
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(ORDER_NO_ORIG);
        auftragNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragNoOrig == null) {
            throw new HurricanServiceCommandException("AuftragNoOrig wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetAuftragLeistungenCommand.properties";
    }
}


