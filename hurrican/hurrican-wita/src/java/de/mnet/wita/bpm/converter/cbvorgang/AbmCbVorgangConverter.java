/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2011 15:52:38
 */
package de.mnet.wita.bpm.converter.cbvorgang;

import static com.google.common.collect.Collections2.*;

import java.time.*;
import java.util.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.AbmMeldungsCode;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Converter-Klasse, um eine ABM (Auftragsbestaetigungsmeldung) auf den {@link WitaCBVorgang} zu uebertragen.
 */
public class AbmCbVorgangConverter extends AbstractMwfCbVorgangConverter<AuftragsBestaetigungsMeldung> {

    @Override
    protected void writeData(WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm) throws StoreException {
        // Die Bemerkung wird in jedem Fall auf den Vorgang geschrieben
        cbVorgang.setReturnBemerkung(readMeldungsPositionen(abm));

        final Date liefertermin = DateConverterUtils.asDate(abm.getVerbindlicherLiefertermin());
        Auftrag witaRequest = mwfEntityService.getAuftragOfCbVorgang(cbVorgang.getId());

        // falls noch keine Rueckmeldung vorhanden oder Realisierungsdatum abweichend, ABM Status weitersetzen
        if ((cbVorgang.getReturnRealDate() == null)
                || !DateTools.isDateEqual(liefertermin, cbVorgang.getReturnRealDate())) {
            cbVorgang.nextAbm();
        }

        cbVorgang.answer(true);
        writeAbmMeldungsPositionen(cbVorgang, abm.getMeldungsPositionen());

        cbVorgang.setReturnRealDate(liefertermin);
        cbVorgang.setReturnVTRNR(abm.getVertragsNummer());
        cbVorgang.setTalRealisierungsZeitfenster(witaDataService.transformWitaZeitfenster(witaRequest, abm));

        checkAndCloseTamUserTask(cbVorgang);
        resetWiedervorlage(cbVorgang);

        writeLeitung(cbVorgang, abm);
    }

    void writeLeitung(WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm) {
        Leitung leitung = abm.getLeitung();
        if (leitung != null) {
            cbVorgang.setReturnLBZ(leitung.getLeitungsBezeichnung().toString());

            List<LeitungsAbschnitt> leitungsAbschnitte = Lists.newArrayList(leitung.getLeitungsAbschnitte());
            Collections.sort(leitungsAbschnitte, Leitung.BY_LAUFENDE_NUMMER);

            String length = Joiner.on("/").join(transform(leitungsAbschnitte, Leitung.GET_LEITUNGS_LAENGE));

            String aqs = Joiner.on("/").join(transform(leitungsAbschnitte, Leitung.GET_LEITUNGS_DURCHMESSER));
            cbVorgang.setReturnLL(CBVorgang.stripLlOrAqs(length));
            cbVorgang.setReturnAQS(CBVorgang.stripLlOrAqs(aqs));
            cbVorgang.setReturnMaxBruttoBitrate(leitung.getMaxBruttoBitrate());
        }
    }

    private void writeAbmMeldungsPositionen(CBVorgang cbVorgang, Set<MeldungsPositionWithAnsprechpartner> positions) {
        boolean kundeForOrt = false;
        Set<String> carrierBearbeiter = new HashSet<String>();
        for (MeldungsPositionWithAnsprechpartner position : positions) {
            carrierBearbeiter.add(getAnsprechpartnerAsString(position));
            if (position.getMeldungsCode().equals(AbmMeldungsCode.CUSTOMER_REQUIRED.meldungsCode)) {
                kundeForOrt = true;
            }
        }
        cbVorgang.setReturnKundeVorOrt(kundeForOrt);
        cbVorgang.setCarrierBearbeiter(getCarrierBearbeiterString(carrierBearbeiter));
    }

    private String getAnsprechpartnerAsString(MeldungsPositionWithAnsprechpartner meldungsPosition) {
        AnsprechpartnerTelekom ansprechpartnerTelekom = meldungsPosition.getAnsprechpartnerTelekom();
        return ansprechpartnerTelekom == null ? "" : ansprechpartnerTelekom.asString();
    }

    String getCarrierBearbeiterString(Set<String> carrierBearbeiter) {
        String carrierBearbeiterString = Joiner.on(",").join(carrierBearbeiter);
        if (StringUtils.isNotBlank(carrierBearbeiterString) && (carrierBearbeiterString.length() > 100)) {
            return carrierBearbeiterString.substring(0, 99);
        }
        return carrierBearbeiterString;
    }

    private void checkAndCloseTamUserTask(WitaCBVorgang cbVorgang) throws StoreException {
        TamUserTask userTask = cbVorgang.getTamUserTask();
        if (userTask == null) {
            return;
        }
        if (!userTask.isTv60Sent()) {
            witaUsertaskService.closeUserTask(userTask);
        }
    }
}
