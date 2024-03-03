/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 12:45:35
 */
package de.mnet.wita.bpm.converter.cbvorgang;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;

/**
 * Erstellt einen neuen TAM-Usertask oder oeffnet einen bestehenden Usertask wieder.
 */
public class TamCbVorgangConverter extends AbstractMwfCbVorgangConverter<TerminAnforderungsMeldung> {

    @Autowired
    private MwfEntityService mwfEntityService;

    @Override
    protected void writeData(WitaCBVorgang cbVorgang, TerminAnforderungsMeldung meldung) {
        TamUserTask tamUserTask = cbVorgang.getTamUserTask();
        if (tamUserTask == null) {
            tamUserTask = new TamUserTask();
        }
        else {
            // existierenden Usertask neu oeffnen, falls er bereits geschlossen ist
            tamUserTask.setStatus(UserTaskStatus.OFFEN);
            tamUserTask.setBearbeiter(null);
            tamUserTask.setTamBearbeitungsStatus(TamBearbeitungsStatus.OFFEN);
            tamUserTask.setLetzteAenderung(new Date());
            tamUserTask.setTv60Sent(false);
            tamUserTask.setWiedervorlageAm((Date)null);
            tamUserTask.setMahnTam(isMahnTam(meldung));
        }
        cbVorgang.setTamUserTask(tamUserTask);
    }

    /**
     * Prueft, ob die angegebene Meldung ein MahnTam ist oder nicht. Mit WitaV7 schickt Telekom ein MahnTam. Davor wurde
     * immer die zweite TAM als MahnTam interpretiert. Hier wird nicht zwischen verschiedenen Wita-Versionen
     * unterschieden, damit wir in der Lage sind falls in Wita V7 Telekom doch zwei TAMs (und nicht TAM und MTAM)
     * hintereinander schickt, die zweite TAM als MTAM zu markieren.
     */
    private boolean isMahnTam(TerminAnforderungsMeldung meldung) {
        return meldung.isMahnTam() || mwfEntityService.isLastMeldungTam(meldung); // Falls Tam direkt auf Tam folgt
    }

}
