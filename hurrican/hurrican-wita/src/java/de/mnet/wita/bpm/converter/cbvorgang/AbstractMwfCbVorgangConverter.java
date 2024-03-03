/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2011 15:46:28
 */
package de.mnet.wita.bpm.converter.cbvorgang;

import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.common.tools.ReflectionTools;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaUsertaskService;
import de.mnet.wita.service.impl.WitaDataService;

public abstract class AbstractMwfCbVorgangConverter<T extends Meldung<?>> {

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    protected CarrierElTALService carrierElTalService;
    @Autowired
    protected WitaUsertaskService witaUsertaskService;
    @Autowired
    protected WitaDataService witaDataService;
    @Autowired
    protected MwfEntityService mwfEntityService;

    private final Class<T> meldungsType;

    protected AbstractMwfCbVorgangConverter() {
        meldungsType = ReflectionTools.getTypeArgument(AbstractMwfCbVorgangConverter.class, this.getClass());
    }

    public void write(WitaCBVorgang cbVorgang, Meldung<?> meldung) throws StoreException {
        writeData(cbVorgang, meldungsType.cast(meldung));
        carrierElTalService.saveCBVorgang(cbVorgang);
    }

    protected abstract void writeData(WitaCBVorgang cbVorgang, T meldung) throws StoreException;

    protected String readMeldungsPositionen(Meldung<?> meldung) {
        StringBuilder sb = new StringBuilder();
        for (MeldungsPosition mp : meldung.getMeldungsPositionen()) {
            sb.append(mp.getMeldungsCode()).append(" : ").append(mp.getMeldungsText()).append("\n");
        }
        return sb.toString();
    }

    public Class<T> getMeldungsType() {
        return meldungsType;
    }

    protected void resetWiedervorlage(WitaCBVorgang cbVorgang) {
        if (cbVorgang != null) {
            cbVorgang.setWiedervorlageAm((Date)null);
            TamUserTask tamUserTask = cbVorgang.getTamUserTask();
            if (tamUserTask != null) {
                tamUserTask.setWiedervorlageAm((Date)null);
            }
        }
    }

}
