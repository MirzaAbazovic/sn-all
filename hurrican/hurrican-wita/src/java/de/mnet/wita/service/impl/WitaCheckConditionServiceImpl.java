/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2011 08:41:43
 */
package de.mnet.wita.service.impl;

import static org.apache.commons.lang.StringUtils.*;

import java.time.*;
import java.util.*;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wita.config.WitaConstants;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaCheckConditionService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaWbciServiceFacade;

@CcTxRequired
public class WitaCheckConditionServiceImpl implements WitaCheckConditionService {

    private static final Logger LOGGER = Logger.getLogger(WitaTalOrderServiceImpl.class);
    @Autowired
    MwfEntityService mwfEntityService;
    @Autowired
    DateTimeCalculationService dateTimeCalculationService;
    @Autowired
    WitaWbciServiceFacade witaWbciServiceFacade;
    @Autowired
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    private WitaDataService witaDataService;

    @Override
    public void checkConditionsForStorno(WitaCBVorgang cbVorgang, Auftrag auftrag) {
        checkQebAndErlmAndAbbmAndAbmIfKlammer(cbVorgang, auftrag, STORNO_ONLY_AFTER_QEB, STORNO_ONLY_BEFORE_ERLM,
                STORNO_ONLY_BEFORE_ABBM, STORNO_WITH_KLAMMER_ONLY_AFTER_ABM);

        boolean hasTam = mwfEntityService.checkMeldungReceived(cbVorgang.getCarrierRefNr(),
                TerminAnforderungsMeldung.class);
        if (!hasTam
                && !dateTimeCalculationService.isStornoPossible(getRealTermin(cbVorgang), auftrag.getGeschaeftsfall()
                .getKundenwunschtermin().getZeitfenster())) {
            throw new WitaUserException(STORNO_TOO_LATE);
        }
    }

    @Override
    public void checkConditionsForTv(WitaCBVorgang cbVorgang, Auftrag auftrag, LocalDate neuerTermin) {
        Preconditions.checkNotNull(neuerTermin, "Es muss ein KWT für die Terminverschiebung gesetzt werden.");
        if (neuerTermin.isBefore(LocalDate.now())) {
            throw new WitaUserException(TV_TERMIN_IN_VERGANGENHEIT);
        }
        checkQebAndErlmAndAbbmAndAbmIfKlammer(cbVorgang, auftrag, TV_ONLY_AFTER_QEB, TV_ONLY_BEFORE_ERLM, TV_ONLY_BEFORE_ABBM,
                TV_WITH_KLAMMER_ONLY_AFTER_ABM);

        Date realDate = getRealTermin(cbVorgang);
        Zeitfenster zeitfenster = auftrag.getGeschaeftsfall().getKundenwunschtermin().getZeitfenster();

        boolean hasTam = mwfEntityService.checkMeldungReceived(cbVorgang.getCarrierRefNr(),
                TerminAnforderungsMeldung.class);

        // Workaround fuer fehlende TAMs von der Telekom, falls das Realsierungdatum in der Vergangheit (Schaltungstag
        // um 16:00 Uhr) liegt gelten die gleichen Termin-Kriterien wie bei einer TAM
        if (realDate != null && isTamTooLate(realDate)) {
            hasTam = true;
        }

        if (!hasTam && !dateTimeCalculationService.isTerminverschiebungPossible(realDate, zeitfenster)) {
            throw new WitaUserException(TV_ONLY_36_HOURS_BEFORE);
        }
        if (!dateTimeCalculationService.isTerminverschiebungValid(neuerTermin, zeitfenster, hasTam,
                cbVorgang.getWitaGeschaeftsfallTyp(), cbVorgang.getVorabstimmungsId(), cbVorgang.isHvtToKvz())) {
            throw new WitaUserException(String.format(TV_MINDESTVORLAUFZEIT,
                    (hasTam) ? WitaConstants.MINDESTVORLAUFZEIT_NACH_TAM : WitaConstants.MINDESTVORLAUFZEIT));
        }

        // checks if the new date is already coordinated over the WBCI-Process
        if (!hasTam && isNotEmpty(cbVorgang.getVorabstimmungsId())) {
            witaWbciServiceFacade.checkDateForMatchingWithVorabstimmung(Date.from(neuerTermin.atStartOfDay(ZoneId.systemDefault()).toInstant()), cbVorgang.getVorabstimmungsId());
        }
    }

    private void checkQebAndErlmAndAbbmAndAbmIfKlammer(WitaCBVorgang cbVorgang, Auftrag auftrag,
            String onlyAfterQebExceptionMessage, String onlyBeforeErlmExceptionMessage,
            String onlyBeforeAbbmExceptionMessage, String onlyAfterAbmIfKlammerExceptionMessage) {
        Preconditions.checkArgument(cbVorgang.getCarrierRefNr().equals(auftrag.getExterneAuftragsnummer()));
        String extAuftragsnr = cbVorgang.getCarrierRefNr();

        MnetWitaRequest mnetWitaRequet = mwfEntityService.findUnsentRequest(cbVorgang.getId());
        if (mnetWitaRequet == null
                && !mwfEntityService.checkMeldungReceived(extAuftragsnr, QualifizierteEingangsBestaetigung.class)) {
            throw new WitaUserException(String.format(onlyAfterQebExceptionMessage, extAuftragsnr));
        }
        if (auftragHasKlammerung(auftrag)
                && !mwfEntityService.checkMeldungReceived(extAuftragsnr, AuftragsBestaetigungsMeldung.class)) {
            throw new WitaUserException(String.format(onlyAfterAbmIfKlammerExceptionMessage, extAuftragsnr));
        }
        if (mwfEntityService.checkMeldungReceived(extAuftragsnr, ErledigtMeldung.class)) {
            throw new WitaUserException(String.format(onlyBeforeErlmExceptionMessage, extAuftragsnr));
        }
        List<AbbruchMeldung> entities = mwfEntityService.findMwfEntitiesByProperty(AbbruchMeldung.class,
                Meldung.EXTERNE_AUFTRAGSNR_FIELD, extAuftragsnr);
        for (AbbruchMeldung abbruchMeldung : entities) {
            if (abbruchMeldung.getAenderungsKennzeichen() == AenderungsKennzeichen.STANDARD) {
                throw new WitaUserException(String.format(onlyBeforeAbbmExceptionMessage, extAuftragsnr));
            }
        }
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public void checkConditionsForWbciPreagreement(List<? extends CBVorgang> createdCbVorgaenge) {
        Set<String> wbciVaIds = new HashSet<>();
        List<WitaCBVorgang> witaCBVorgaenge = new ArrayList<>();

        for (CBVorgang cbVorgang : createdCbVorgaenge) {
            if (cbVorgang instanceof WitaCBVorgang) {
                String wbciVaId = ((WitaCBVorgang) cbVorgang).getVorabstimmungsId();
                if (isNotEmpty(wbciVaId)) {
                    wbciVaIds.add(wbciVaId);
                    witaCBVorgaenge.add((WitaCBVorgang) cbVorgang);
                }
            }
        }
        if (wbciVaIds.size() > 1) {
            throw new WitaUserException(WBCI_MORE_THEN_ONE_VA_ID);
        }
        else if (wbciVaIds.size() == 1 && !witaCBVorgaenge.isEmpty()) {
            String wbciVaId = wbciVaIds.iterator().next();

            //für mehere WITA-Vorgänge ist nur ein gleicher GF möglich
            GeschaeftsfallTyp witaGeschaeftsfallTyp = witaCBVorgaenge.iterator().next().getWitaGeschaeftsfallTyp();

            //keine WBCI checks fuer Fax-bezogene IDs, da fuer diese kein WBCI Vorgang,
            // sondern lediglich eine ID erstellt wird
            if (!WbciConstants.isFaxRouting(wbciVaId)) {
                //checks state and open wbci requests
                witaWbciServiceFacade.checkVorabstimmungValidForWitaVorgang(witaGeschaeftsfallTyp, wbciVaId);
                //checks realdate and rufnummern for matching with WBCI-Vorabstimmung
                Set<Rufnummer> rufnummern = new HashSet<>();
                for (WitaCBVorgang witaCBVorgang : witaCBVorgaenge) {
                    if (witaCBVorgang.isKuendigung()) {
                        // spezieller Check, dass Kuendigungsdatum in WITA >= WBCI Wechseltermin ist!
                        // (notwendig, da in WITA Wizard das Kuendigungsdatum z.T. automatisch angepasst wird, wenn
                        // die benoetigte Vorlaufzeit unterschritten ist) (siehe WITA-2393)
                        witaWbciServiceFacade.checkDateIsEqualOrAfterWbciVa(getRealTermin(witaCBVorgang), wbciVaId);
                    }
                    else {
                        witaWbciServiceFacade.checkDateForMatchingWithVorabstimmung(getRealTermin(witaCBVorgang), wbciVaId);
                        rufnummern.addAll(witaDataService.loadRufnummern(witaCBVorgang));
                    }
                }
                witaWbciServiceFacade.checkRufnummernForMatchingWithVorabstimmung(rufnummern, wbciVaId);
            }
        }
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public void checkConditionsForAbm(WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm) throws StoreException {
        try {
            checkWbciConditionsForAbm(cbVorgang, abm);
            checkAbmLieferterminForProviderwechsel(cbVorgang, abm);
        }
        catch (WbciValidationException e) {
            LOGGER.info(e.getMessage(), e);
            witaTalOrderService.markWitaCBVorgangAsKlaerfall(cbVorgang.getId(), e.getMessage());
        }
    }

    /**
     * checks if the WBCI-VA-ID is set, that the dates are matching *
     */
    protected void checkWbciConditionsForAbm(WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm)
            throws WbciValidationException {
        if (isNotEmpty(cbVorgang.getVorabstimmungsId())) {
            witaWbciServiceFacade.checkDateForMatchingWithVorabstimmung(Date.from(abm.getVerbindlicherLiefertermin().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    cbVorgang.getVorabstimmungsId());
        }
    }

    /**
     * Prueft, ob der bestaetigte Liefertermin im Falle eines Anbieterwechsels ein gueltiger Tag ist. <br/>
     * Dies ist dann der Fall, wenn der angegebene Termin sowie der darauffolgende Tag Arbeitstage sind. <br/>
     * Der Check wird nur durchgefuehrt, wenn folgende Bedingungen erfuellt sind:
     * 
     * <pre>
     *     - WITA GF ist Verbundleistung bzw. Anbieterwechsel
     *     ODER
     *     - WITA GF ist 'NEU' und Montageleistung beinhaltet "#abw#"
     * </pre>
     * 
     * @param cbVorgang
     * @param abm
     */
    protected void checkAbmLieferterminForProviderwechsel(WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm)
            throws WbciValidationException {
        if (cbVorgang.isWitaGfAnbieterwechsel()
                && cbVorgang.getVorabstimmungsId() == null
                && !DateCalculationHelper.isWorkingDayAndNextDayNotHoliday(abm.getVerbindlicherLiefertermin())) {
            throw new WbciValidationException(
                    "Der bestätigte Liefertermin liegt auf einem schaltfreien Tag oder auf einem Tag VOR einem schaltfreien Tag. "
                            +
                            "Dies ist bei einem Anbieterwechsel nicht zulässig! Bitte mit abgebendem Provider klären.");
        }
    }

    /**
     * public for testing
     */
    public boolean isTamTooLate(Date realDate) {
        return Instant.ofEpochMilli(realDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(16).isBefore(LocalDateTime.now());
    }

    private boolean auftragHasKlammerung(Auftrag auftrag) {
        return (auftrag != null) && (auftrag.getAuftragsKenner() != null)
                && (auftrag.getAuftragsKenner().getAnzahlAuftraege() != null)
                && (auftrag.getAuftragsKenner().getAnzahlAuftraege() > 1);
    }

    protected Date getRealTermin(WitaCBVorgang cbVorgang) {
        Date realDate = cbVorgang.getReturnRealDate();
        if (realDate == null) {
            realDate = cbVorgang.getVorgabeMnet();
        }
        return realDate;
    }

}
