/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.02.2012 09:29:35
 */
package de.mnet.wita.ticketing.converter;

import static com.google.common.collect.Iterables.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.service.WitaUsertaskService;

public abstract class AbstractMeldungPvBsiProtokollConverter<T extends Meldung<?>> extends
        BaseMeldungBsiProtokollConverter<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractMeldungPvBsiProtokollConverter.class);

    @Autowired
    private WitaUsertaskService witaUsertaskService;

    @Override
    public Long findHurricanAuftragId(T meldung) {
        AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTask(meldung.getExterneAuftragsnummer());
        if (userTask == null) {
            throw new RuntimeException(String.format(
                    "Kein AkmPvUserTask zu der zu protokollierenden Meldung gefunden (Id: %s; ext. Auftragsnr: %s)",
                    meldung.getId(), meldung.getExterneAuftragsnummer()));
        }
        try {
            return getOnlyElement(userTask.getAuftragIds());
        }
        catch (NoSuchElementException e) {
            LOGGER.info("Problem: Keine AuftragDaten zum AkmPvUserTask der zu protokollierenden Meldung gefunden"
                    + "; Nachricht: " + meldung.toString(), e);
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e) {
            LOGGER.info("Problem: Mehrere AuftragDaten zum AkmPvUserTask der zu protokollierenden Meldung gefunden"
                    + "; Nachricht: " + meldung.toString(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getAnbieterwechsel46TKG(String extAuftragsnummer) {
        return "";
    }
}
