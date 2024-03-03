/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 13:35:09
 */
package de.mnet.wita.aggregator;

import static de.augustakom.common.tools.lang.TelefonnummerUtils.*;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.validation.EMailValidator;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Ansprechpartner.Typ;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.mnet.wita.aggregator.utils.AggregationAddressHelper;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die {@link Montageleistung} zu ermitteln. Dabei wird der Ansprechpartner der Endstelle
 * ermittelt, auf der der WITA-Vorgang ausgeloest wird.
 */
public class MontageleistungAggregator extends AbstractWitaDataAggregator<Montageleistung> {

    private static final Logger LOG = Logger.getLogger(MontageleistungAggregator.class);

    private static final String MONTAGEHINWEIS_HVT_KVZ = "KUE TAL [%s]%s";

    @Resource(name = "de.augustakom.hurrican.service.cc.AnsprechpartnerService")
    protected AnsprechpartnerService ansprechpartnerService;

    @Override
    public Montageleistung aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        try {
            Typ ansprechpartnerTyp = loadAnsprechpartnerTyp(cbVorgang);
            List<Ansprechpartner> ansprechpartner = ansprechpartnerService.findAnsprechpartner(ansprechpartnerTyp,
                    cbVorgang.getAuftragId());

            Montageleistung montageleistung = createMontageleistung(ansprechpartner, cbVorgang);
            if (montageleistung == null) {
                ansprechpartner = loadAnsprechpartnerFromWholeOrder(cbVorgang.getAuftragId(), ansprechpartnerTyp);
                montageleistung = createMontageleistung(ansprechpartner, cbVorgang);
            }

            if ((montageleistung == null) && StringUtils.isNotBlank(cbVorgang.getMontagehinweis())) {
                throw new WitaDataAggregationException(
                        "Es wurde ein Montagehinweis angegeben, aber es ist kein gültiger Ansprechpartner für die Montageleistung vorhanden. Erfassen Sie einen Ansprechpartner auf der richtigen Endstelle mit Adresse und Telefonnummer.");
            }

            return montageleistung;
        }
        catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Bei der Ermittlung der Montageleistung ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    private Montageleistung createMontageleistung(List<Ansprechpartner> ansprechpartner, WitaCBVorgang cbVorgang) {
        try {
            if (CollectionTools.isNotEmpty(ansprechpartner)) {
                Ansprechpartner endstelleAnsprechpartner = ansprechpartner.get(0); // ersten Ansprechpartner verwenden!
                CCAddress address = endstelleAnsprechpartner.getAddress();

                if (address != null) {
                    Personenname personenname = new Personenname();
                    Anrede anrede = AggregationAddressHelper.getAnredeForAddress(address);

                    // AM-Projekte erfasst alle Ansprechpartner als Business, deshalb muss
                    // hier die Anrede umgesetzt werden!
                    if (!Anrede.personAnreden.contains(anrede)) {
                        anrede = Anrede.UNBEKANNT;
                    }
                    personenname.setAnrede(anrede);
                    personenname.setVorname(address.getVorname());
                    personenname.setNachname(address.getName());

                    Montageleistung montageleistung = new Montageleistung();
                    montageleistung.setPersonenname(personenname);
                    montageleistung.setTelefonnummer(convertTelefonnummer(address.getTelefon()));
                    if (StringUtils.isBlank(montageleistung.getTelefonnummer())) {
                        montageleistung.setTelefonnummer(convertTelefonnummer(address.getHandy()));
                    }
                    montageleistung.setEmailadresse(getFirstValidEMail(address.getEmail()));
                    montageleistung.setMontagehinweis(cbVorgang.getMontagehinweis());
                    montageleistung.setTerminReservierungsId(cbVorgang.getTerminReservierungsId());

                    appendVbzOfHvtOrder(montageleistung, cbVorgang);
                    return montageleistung;
                }
            }
        }
        catch (WitaDataAggregationException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Fehler bei der Ermittlung des Ansprechpartners (Montageleistung) zum Auftrag: " + e.getMessage(),
                    e);
        }
        return null;
    }

    /**
     * Falls es sich um einen HVT->KVZ Wechsel handelt wird in dieser Methode die Leitungsbezeichnung des HVT-Auftrags
     * ermittelt und in den Montagehinweis zusaetzlich eingetragen.
     */
    void appendVbzOfHvtOrder(Montageleistung montageleistung, WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        if (cbVorgang.isNeuschaltung() && cbVorgang.getCbVorgangRefId() != null) {
            try {
                // LBZ von zu kuendigendem Auftrag ermitteln und als Montageleistung setzen
                Carrierbestellung hvtCb = witaDataService.getCarrierbestellungReferencedByCbVorgang(cbVorgang.getCbVorgangRefId());
                if (hvtCb == null || StringUtils.isBlank(hvtCb.getLbz())) {
                    throw new WitaDataAggregationException(
                            String.format("Auf dem zu kündigenden HVt Auftrag konnte keine Leitungsbezeichnung ermittelt werden.%n" +
                                    "Diese ist für eine Umschaltung HVt -> KVz jedoch zwingend notwendig!")
                    );
                }

                String montagehinweis = (StringUtils.isNotBlank(montageleistung.getMontagehinweis()))
                        ? " " + montageleistung.getMontagehinweis() : "";
                montageleistung.setMontagehinweis(
                        String.format(MONTAGEHINWEIS_HVT_KVZ, hvtCb.getLbz(), montagehinweis));
            }
            catch (WitaDataAggregationException e) {
                throw e;
            }
            catch (WitaBaseException e) {
                throw new WitaDataAggregationException(
                        "Leitungsbezeichnung der zu kündigenden HVt TAL konnte nicht ermittelt werden!", e);
            }
        }
    }

    /**
     * Ermittelt weitere Hurrican-Auftraege, die zu dem angegebenen Auftrag gehoeren und ermittelt von diesen die
     * Ansprechpartner des Typs {@code ansprechpartnerTyp}. (Dies ist notwendig, da z.B. bei Connect-Auftraegen mit
     * mehreren Leitungen immer nur auf einem Hurrican-Auftrag der Ansprechpartner definiert wird.)
     */
    List<Ansprechpartner> loadAnsprechpartnerFromWholeOrder(Long auftragId, Typ ansprechpartnerTyp)
            throws FindException {
        List<Ansprechpartner> ansprechpartner = new ArrayList<>();

        for (AuftragDatenView adView : loadAuftragFromWholeOrder(auftragId)) {
            CollectionTools.addAllIgnoreNull(ansprechpartner,
                    ansprechpartnerService.findAnsprechpartner(ansprechpartnerTyp, adView.getAuftragId()));
        }
        return ansprechpartner;
    }

    Typ loadAnsprechpartnerTyp(CBVorgang cbVorgang) {
        List<Endstelle> endstellen = witaDataService.loadEndstellen(cbVorgang);
        if (endstellen.isEmpty()) {
            throw new WitaDataAggregationException("Es konnten keine Endstellen ermittelt werden.");
        }
        return (endstellen.get(0).isEndstelleA()) ? Typ.ENDSTELLE_A : Typ.ENDSTELLE_B;
    }

    String getFirstValidEMail(String addressMail) {
        if (StringUtils.isNotBlank(addressMail)) {
            if (EMailValidator.getInstance().isValid(addressMail)) {
                return addressMail;
            }
            Iterable<String> splitted = Splitter.on(CharMatcher.anyOf(";,")).split(addressMail);
            for (String split : splitted) {
                if (EMailValidator.getInstance().isValid(split)) {
                    return split;
                }
            }
        }
        return null;
    }

}
