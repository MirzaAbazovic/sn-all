/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 10:21:46
 */
package de.mnet.wita.bpm.converter.cbvorgang;

import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.*;

import java.time.format.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.Positionsattribute;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Converter-Klasse, um eine ABBM (Abbruchmeldung) auf den {@link WitaCBVorgang} zu uebertragen.
 */
public class AbbmCbVorgangConverter extends AbstractMwfCbVorgangConverter<AbbruchMeldung> {

    @Override
    protected void writeData(WitaCBVorgang cbVorgang, AbbruchMeldung abbm) throws StoreException {
        // Die Bemerkung wird in jedem Fall auf den Vorgang geschrieben
        cbVorgang.setReturnBemerkung(readMeldungsPositionen(abbm));

        // ABBM auf Bereitstellung ist immer eine negative Rueckmeldung
        if (abbm.getAenderungsKennzeichen() == STANDARD) {
            cbVorgang.answer(false);
        }
        // ABBM auf Storno/Terminverschiebung setzt den CBVorgang zurueck
        // (bedeutet: die ABBM wird fuer die Storno/TV geschickt, nicht fuer den zuvor ausgeloesten Vorgang!)
        else {
            if (abbm.getAenderungsKennzeichen() == TERMINVERSCHIEBUNG) {
                // Setze Vorgabedatum zurueck, falls vorhanden
                cbVorgang.setVorgabeMnet(cbVorgang.getPreviousVorgabeMnet());
            }
            if (cbVorgang.hasReturnRealDate()) {
                // Wenn schon ein Realisierungsdatum gesetzt ist, dann war schon eine ABM da -> beantwortet
                cbVorgang.answer(true);
            }
            else {
                // CBVorgang wird wieder geoeffnet; somit ist der urspruengliche Vorgang wieder sichtbar
                cbVorgang.open(CBVorgang.STATUS_TRANSFERRED);
            }
        }

        checkAndCloseTamUserTask(cbVorgang, abbm); // TODO acceptancetest
        resetWiedervorlage(cbVorgang);

        // Nach der ABBM ist das Aenderungskennzeichen auf jeden Fall Standard
        cbVorgang.setLetztesGesendetesAenderungsKennzeichen(cbVorgang.getAenderungsKennzeichen());
        cbVorgang.setAenderungsKennzeichen(STANDARD);
    }

    private void checkAndCloseTamUserTask(WitaCBVorgang cbVorgang, AbbruchMeldung abbm) throws StoreException {
        TamUserTask tamUserTask = cbVorgang.getTamUserTask();
        if (tamUserTask == null) {
            return;
        }
        switch (abbm.getAenderungsKennzeichen()) {
            case STANDARD:
                witaUsertaskService.closeUserTask(tamUserTask);
                break;
            case TERMINVERSCHIEBUNG:
            case STORNO:
                // schließenden, wenn TV/Storno mit folgendem Code abgelehnt
                for (MeldungsPosition mpos : abbm.getMeldungsPositionen()) {
                    if (AbbmMeldungsCode.AUFTRAG_AUSGEFUEHRT_TV_NOT_POSSIBLE.meldungsCode
                            .equals(mpos.getMeldungsCode())
                            || AbbmMeldungsCode.AUFTRAG_AUSGEFUEHRT_STORNO_NOT_POSSIBLE.meldungsCode.equals(mpos
                            .getMeldungsCode())) {
                        witaUsertaskService.closeUserTask(tamUserTask);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected String readMeldungsPositionen(Meldung<?> meldung) {
        StringBuilder sb = new StringBuilder();
        for (MeldungsPosition mpos : meldung.getMeldungsPositionen()) {
            sb.append(mpos.getMeldungsCode()).append(" : ").append(mpos.getMeldungsText()).append("\n");

            Positionsattribute positionsattribute = mpos.getPositionsattribute();
            if (positionsattribute != null) {
                if (StringUtils.isNotBlank(positionsattribute.getAlternativprodukt())) {
                    sb.append("Alternativprodukt: ").append(positionsattribute.getAlternativprodukt()).append("\n");
                }
                if (positionsattribute.getErledigungsterminOffenerAuftrag() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR);
                    sb.append("Erledigungstermin offener Auftrag: ")
                            .append(formatter.format(positionsattribute.getErledigungsterminOffenerAuftrag()))
                            .append("\n");
                }
                if (StringUtils.isNotBlank(positionsattribute.getFehlauftragsnummer())) {
                    sb.append("Fehlauftragsnummer: ").append(positionsattribute.getFehlauftragsnummer()).append("\n");
                }
                if (CollectionTools.isNotEmpty(positionsattribute.getDoppeladerBelegt())) {
                    sb.append("Doppelader belegt:\n");
                    for (LeitungsBezeichnung lbz : positionsattribute.getDoppeladerBelegt()) {
                        sb.append(lbz.getLeitungsbezeichnungString()).append("\n");
                    }
                }
                if (positionsattribute.getStandortKundeKorrektur() != null) {
                    sb.append(positionsattribute.getStandortKundeKorrektur().toString()).append("\n");
                }
                if (positionsattribute.getAnschlussPortierungKorrekt() != null) {
                    sb.append(positionsattribute.getAnschlussPortierungKorrekt().toString()).append("\n");
                }
            }
        }
        return sb.toString();
    }

}
