/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2005 15:32:24
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CounterService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Command-Klasse, um einen Auftrag mehrmals zu kopieren.
 *
 *
 */
@CcTxRequired
public class CopyAuftragCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(CopyAuftragCommand.class);

    public static final String KEY_SESSION_ID = "session.id";
    public static final String KEY_AK_USER = "ak.user";
    public static final String KEY_AUFTRAGID_2_COPY = "auftrag.id2copy";
    public static final String KEY_PARENT_AUFTRAG_ID = "parent.auftrag.id";
    public static final String KEY_BUENDEL_NR = "buendel.nr";
    public static final String KEY_BUENDEL_HERKUNFT = "buendel.herkunft";
    public static final String KEY_COPY_COUNT = "copy.count";
    public static final String KEY_VORGABE_SCV = "vorgabe.scv";
    public static final String KEY_COPY_ES = "copy.es";

    private AKUser user = null;
    private Long sessionId = null;
    private Long auftragId2Copy = null;
    private Long parentAuftragId = null;
    private Integer buendelNr = null;
    private String buendelHerkunft = null;
    private int copyCount = -1;
    private Date vorgabeSCV = null;
    private boolean copyES = false;

    private Produkt produkt2Copy = null;
    private Auftrag auftrag2Copy = null;
    private AuftragDaten ad2Copy = null;
    private AuftragTechnik at2Copy = null;
    private Endstelle esA2Copy = null;
    private Endstelle esB2Copy = null;
    private EndstelleLtgDaten esLtgA2Copy = null;
    private EndstelleLtgDaten esLtgB2Copy = null;
    private AuftragDaten parentAuftrag = null;
    private boolean createNewBuendel = false;

    private List<Auftrag> copies = null;

    @Override
    public Object execute() throws Exception {
        checkValues();
        loadValues();
        copyAuftrag();
        return copies;
    }

    /*
     * Kopiert den angegebenen Auftrag.
     * @throws StoreException
     */
    private void copyAuftrag() throws ServiceCommandException, StoreException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            EndstellenService esSrv = getCCService(EndstellenService.class);

            if (createNewBuendel) {
                CounterService cs = getCCService(CounterService.class);
                buendelNr = cs.getNewIntValue(CounterService.COUNTER_BUENDEL);
                buendelHerkunft = AuftragDaten.BUENDEL_HERKUNFT_HURRICAN;

                if ((buendelNr == null) || (buendelNr.intValue() <= 0)) {
                    throw new HurricanServiceCommandException("Es konnte keine Bündel-Nr erzeugt werden!");
                }
                setBuendel2Auftrag(buendelNr, buendelHerkunft, parentAuftrag, as);
                setBuendel2Auftrag(buendelNr, buendelHerkunft, ad2Copy, as);
            }

            copies = new ArrayList<Auftrag>();
            for (int i = 0; i < copyCount; i++) {
                AuftragDaten newAD = new AuftragDaten();
                HistoryHelper.setHistoryData(newAD, new Date());
                newAD.setStatusId(AuftragStatus.ERFASSUNG_SCV);
                newAD.setVorgabeSCV(vorgabeSCV);
                newAD.setBearbeiter(user.getName());
                newAD.setBemerkungen(ad2Copy.getBemerkungen());
                newAD.setBestellNr(ad2Copy.getBestellNr());
                newAD.setLbzKunde(ad2Copy.getLbzKunde());
                newAD.setBuendelNr(buendelNr);
                newAD.setBuendelNrHerkunft(buendelHerkunft);
                newAD.setProdId(ad2Copy.getProdId());
                newAD.setMmzId(ad2Copy.getMmzId());

                AuftragTechnik newAT = new AuftragTechnik();
                PropertyUtils.copyProperties(newAT, at2Copy);
                HistoryHelper.setHistoryData(newAT, new Date());
                newAT.setVbzId(null);
                newAT.setAuftragTechnik2EndstelleId(null);

                Auftrag auftrag = as.createAuftrag(auftrag2Copy.getKundeNo(),
                        newAD, newAT, sessionId, null);
                copies.add(auftrag);
                LOGGER.info("----->>>> erzeugte Auftrags-Kopie: " + auftrag.getId());

                List<Endstelle> endstellen = esSrv.createEndstellen(newAT, produkt2Copy.getEndstellenTyp(), sessionId);
                if ((endstellen != null) && copyES) {
                    Endstelle esA = getEndstelle(Endstelle.ENDSTELLEN_TYP_A, endstellen);
                    copyESDaten(esA, auftrag2Copy.getId(), newAT.getAuftragId(), esA2Copy, esLtgA2Copy);

                    Endstelle esB = getEndstelle(Endstelle.ENDSTELLEN_TYP_B, endstellen);
                    copyESDaten(esB, auftrag2Copy.getId(), newAT.getAuftragId(), esB2Copy, esLtgB2Copy);
                }
            }
        }
        catch (ServiceCommandException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /*
     * Kopiert die Endstellen-Daten.
     * @param newES
     * @param source
     * @param sourceLtg
     * @param sourceEsAnsp
     * @throws StoreException
     */
    private void copyESDaten(Endstelle newES, Long auftragIdOld, Long auftragIdNew, Endstelle source,
            EndstelleLtgDaten sourceLtg)
            throws StoreException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ValidationException, ServiceNotFoundException {
        EndstellenService endstellenService = getCCService(EndstellenService.class);
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);

        if (newES != null) {
            if (source != null) {
                endstellenService.copyEndstelle(source, newES, Endstelle.COPY_DEFAULT);
            }

            if (sourceLtg != null) {
                EndstelleLtgDaten newLtg = new EndstelleLtgDaten();
                PropertyUtils.copyProperties(newLtg, sourceLtg);
                HistoryHelper.setHistoryData(newLtg, new Date());
                newLtg.setEndstelleId(newES.getId());
                endstellenService.saveESLtgDaten(newLtg, false);
            }

            Ansprechpartner.Typ ansprechpartnerTyp = (newES.isEndstelleB()) ? ENDSTELLE_B : ENDSTELLE_A;
            ansprechpartnerService.copyAnsprechpartner(ansprechpartnerTyp, auftragIdOld, auftragIdNew);
        }
    }

    /*
     * Uebergibt dem Auftrag <code>ad</code> die Buendel-Daten und speichert den Auftrag ab.
     * @param buendelNr
     * @param buendelHerk
     * @param ad
     * @throws StoreException
     */
    private void setBuendel2Auftrag(Integer buendelNr, String buendelHerk, AuftragDaten ad, CCAuftragService as) throws StoreException {
        if ((ad != null) && (buendelNr != null) && (buendelNr.intValue() > 0)) {
            ad.setBuendelNr(buendelNr);
            ad.setBuendelNrHerkunft(buendelHerk);
            as.saveAuftragDaten(ad, false);
        }
    }

    /* Laedt die benoetigten Daten des zu kopierenden Auftrags. */
    private void loadValues() throws ServiceCommandException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            ProduktService prodService = getCCService(ProduktService.class);

            auftrag2Copy = as.findAuftragById(auftragId2Copy);
            ad2Copy = as.findAuftragDatenByAuftragId(auftragId2Copy);
            at2Copy = as.findAuftragTechnikByAuftragId(auftragId2Copy);
            produkt2Copy = prodService.findProdukt4Auftrag(auftragId2Copy);

            if ((auftrag2Copy == null) || (ad2Copy == null) || (at2Copy == null) || (produkt2Copy == null)) {
                throw new HurricanServiceCommandException("Die zu kopierenden Auftrags-Daten konnten nicht " +
                        "ermittelt werden!");
            }

            Long status = ad2Copy.getStatusId();
            if (NumberTools.isIn(status, new Number[] { AuftragStatus.ABSAGE, AuftragStatus.STORNO }) ||
                    (status.longValue() >= AuftragStatus.KUENDIGUNG.longValue())) {
                throw new HurricanServiceCommandException("Der zu kopierende Auftrag befindet sich in einem " +
                        "ungueltigen Status.");
            }

            if ((ad2Copy.getBuendelNr() != null) && (ad2Copy.getBuendelNr().intValue() > 0)) {
                buendelNr = ad2Copy.getBuendelNr();
                buendelHerkunft = ad2Copy.getBuendelNrHerkunft();
                createNewBuendel = false;
            }

            if (copyES) {
                EndstellenService esSrv = getCCService(EndstellenService.class);
                esA2Copy = esSrv.findEndstelle(at2Copy.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_A);
                esB2Copy = esSrv.findEndstelle(at2Copy.getAuftragTechnik2EndstelleId(), Endstelle.ENDSTELLEN_TYP_B);

                if (esA2Copy != null) {
                    esLtgA2Copy = esSrv.findESLtgDaten4ES(esA2Copy.getId());
                }

                if (esB2Copy != null) {
                    esLtgB2Copy = esSrv.findESLtgDaten4ES(esB2Copy.getId());
                }
            }

            if (parentAuftragId != null) {
                parentAuftrag = as.findAuftragDatenByAuftragId(parentAuftragId);
                if ((parentAuftrag != null) && (parentAuftrag.getBuendelNr() != null) && (parentAuftrag.getBuendelNr().intValue() > 0)) {
                    if ((buendelNr != null) && (buendelNr.intValue() > 0) &&
                            NumberTools.notEqual(parentAuftrag.getBuendelNr(), buendelNr)) {
                        throw new HurricanServiceCommandException("Die Bündel-Nummern des Parent-Auftrags und " +
                                "des zu kopierenden Auftrags sind unterschiedlich. " +
                                "Auftrags-Kopien koennen nicht erstellt werden!");
                    }

                    buendelNr = parentAuftrag.getBuendelNr();
                    buendelHerkunft = parentAuftrag.getBuendelNrHerkunft();
                    createNewBuendel = false;
                }
            }
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Die zu kopierenden Daten konnten nicht ermittelt werden!", e);
        }
    }

    /* Ueberprueft, ob alle benoetigten Daten vorhanden sind. */
    private void checkValues() throws ServiceCommandException, FindException {
        sessionId = getPreparedValue(KEY_SESSION_ID, Long.class, false, "Session-ID not defined!");
        user = (AKUser) getPreparedValue(KEY_AK_USER);
        auftragId2Copy = (Long) getPreparedValue(KEY_AUFTRAGID_2_COPY);
        if (auftragId2Copy == null) {
            throw new HurricanServiceCommandException("Es wurde kein Auftrag angegeben, der kopiert werden soll!");
        }

        parentAuftragId = (Long) getPreparedValue(KEY_PARENT_AUFTRAG_ID);
        buendelNr = (Integer) getPreparedValue(KEY_BUENDEL_NR);
        buendelHerkunft = (String) getPreparedValue(KEY_BUENDEL_HERKUNFT);

        if ((buendelNr == null) || (buendelNr.intValue() == 0)) {
            createNewBuendel = true;
        }

        Integer tmpCount = (Integer) getPreparedValue(KEY_COPY_COUNT);
        copyCount = (tmpCount != null) ? tmpCount.intValue() : -1;
        if (copyCount <= 0) {
            throw new HurricanServiceCommandException("Es wurde kein korrekter Wert für die Anzahl Kopien angegeben!");
        }

        vorgabeSCV = (Date) getPreparedValue(KEY_VORGABE_SCV);
        Boolean tmpCopyES = (Boolean) getPreparedValue(KEY_COPY_ES);
        copyES = (tmpCopyES != null) ? tmpCopyES.booleanValue() : false;
    }

    /*
     * Sucht aus der Liste <code>source</code> die Endstelle vom Typ <code>esTyp</code>
     * heraus und gibt diese zurueck.
     * @param esTyp
     * @param source
     * @return
     */
    private Endstelle getEndstelle(String esTyp, List<Endstelle> source) {
        if (source != null) {
            for (Endstelle es : source) {
                if (StringUtils.equals(esTyp, es.getEndstelleTyp())) {
                    return es;
                }
            }
        }
        return null;
    }
}


