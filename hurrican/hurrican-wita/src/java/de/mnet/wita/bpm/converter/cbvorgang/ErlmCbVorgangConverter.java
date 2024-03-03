/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 12:45:35
 */
package de.mnet.wita.bpm.converter.cbvorgang;

import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.*;

import java.time.*;
import java.util.*;

import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Converter-Klasse, um eine ERLM (Erledigtmeldung) auf den {@link WitaCBVorgang} zu uebertragen.
 */
public class ErlmCbVorgangConverter extends AbstractMwfCbVorgangConverter<ErledigtMeldung> {

    /**
     * Eine ERLM schliesst den CB-Vorgang ab, sofern er vom Typ 'Storno' ist. Wenn der CBVorgang vom Typ Storno ist, die
     * Erlm aber auf den Auftrag kommt, so ist das Storno abgebrochen und die alten Daten werden auf den CBVorgang
     * geschrieben.<br> Eine ERLM auf eine Terminverschiebung (oder einen anderen Vorgangstyp) ist dagegen ganz normal
     * zu prozessieren.
     */
    @Override
    protected void writeData(WitaCBVorgang cbVorgang, ErledigtMeldung meldung) throws StoreException {
        if (!cbVorgang.isAnswered()) {
            cbVorgang.answer(cbVorgang.getReturnOk() == null ? true : cbVorgang.getReturnOk());
        }

        // Bei Erlm zum Ursprungs-Auftrag bei Storno, Storno rueckgaengig machen
        resetIfAenderungskennzeichenIsDifferent(STORNO, cbVorgang, meldung);

        checkAndCloseTamUserTask(cbVorgang);
        resetWiedervorlage(cbVorgang);
    }

    private void resetIfAenderungskennzeichenIsDifferent(AenderungsKennzeichen aenderungsKennzeichen,
            WitaCBVorgang cbVorgang, ErledigtMeldung meldung) {
        if ((cbVorgang.getAenderungsKennzeichen() == aenderungsKennzeichen)
                && (aenderungsKennzeichen != meldung.getAenderungsKennzeichen())) {
            cbVorgang.setAenderungsKennzeichen(AenderungsKennzeichen.STANDARD);
            cbVorgang.setReturnRealDate(Date.from(meldung.getErledigungstermin().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }

    private void checkAndCloseTamUserTask(WitaCBVorgang cbVorgang) throws StoreException {
        TamUserTask userTask = cbVorgang.getTamUserTask();
        if (userTask == null) {
            return;
        }
        witaUsertaskService.closeUserTask(userTask);
    }
}
