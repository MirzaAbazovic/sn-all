/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2011 15:02:50
 */
package de.mnet.wita.bpm.converter;

import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.bpm.converter.cbvorgang.AbstractMwfCbVorgangConverter;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.WitaCBVorgang;

public class MwfCbVorgangConverterService {

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    CarrierElTALService carrierElTalService;

    @Autowired
    List<AbstractMwfCbVorgangConverter<? extends Meldung<?>>> mwfCbVorgangConverters;

    private final Map<Class<? extends Meldung<?>>, AbstractMwfCbVorgangConverter<?>> meldungsTyp2Converter = new HashMap<Class<? extends Meldung<?>>, AbstractMwfCbVorgangConverter<?>>();

    @PostConstruct
    public void init() {
        for (AbstractMwfCbVorgangConverter<?> convertor : mwfCbVorgangConverters) {
            meldungsTyp2Converter.put(convertor.getMeldungsType(), convertor);
        }
    }

    public void write(WitaCBVorgang cbVorgang, Meldung<?> meldung) throws StoreException {
        Class<? extends Meldung<?>> meldungsClass = meldung.getMeldungsTyp().getMeldungClass();
        if (meldungsTyp2Converter.containsKey(meldungsClass)) {
            AbstractMwfCbVorgangConverter<?> converter = meldungsTyp2Converter.get(meldungsClass);
            converter.write(cbVorgang, meldung);
        }
        carrierElTalService.saveCBVorgang(cbVorgang);
    }

    public void writeStorno(WitaCBVorgang cbVorgang, AKUser user) throws StoreException {
        cbVorgang.setLetztesGesendetesAenderungsKennzeichen(cbVorgang.getAenderungsKennzeichen());
        cbVorgang.setAenderungsKennzeichen(STORNO);

        cbVorgang.setSubmittedAt(new Date());
        cbVorgang.setBearbeiter(user);
        cbVorgang.setWiedervorlageAm((Date)null);

        cbVorgang.setStatusLast(cbVorgang.getStatus());
        openCbVorgangIfClosed(cbVorgang, CBVorgang.STATUS_SUBMITTED);

        carrierElTalService.saveCBVorgang(cbVorgang);
    }

    public void writeTerminVerschiebung(WitaCBVorgang cbVorgang, LocalDate termin, AKUser user) throws StoreException {
        cbVorgang.setPreviousVorgabeMnet(cbVorgang.getVorgabeMnet());
        cbVorgang.setVorgabeMnet(Date.from(termin.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Terminverschiebung setzt ABM Status zurueck
        cbVorgang.resetAbmState();

        cbVorgang.setLetztesGesendetesAenderungsKennzeichen(cbVorgang.getAenderungsKennzeichen());
        cbVorgang.setAenderungsKennzeichen(TERMINVERSCHIEBUNG);

        cbVorgang.setSubmittedAt(new Date());
        cbVorgang.setBearbeiter(user);
        cbVorgang.setWiedervorlageAm((Date)null);

        cbVorgang.setStatusLast(cbVorgang.getStatus());
        openCbVorgangIfClosed(cbVorgang, CBVorgang.STATUS_SUBMITTED);

        carrierElTalService.saveCBVorgang(cbVorgang);
    }

    private void openCbVorgangIfClosed(WitaCBVorgang cbVorgang, Long newStatus) {
        if (cbVorgang.isClosed()) {
            cbVorgang.open(newStatus);
        }
    }
}
