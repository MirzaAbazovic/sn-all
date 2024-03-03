/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;
import java.util.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciVersion;

/**
 * Basic meldung builder handles basic properties of Meldung entity.
 *
 *
 */
public abstract class MeldungBuilder<T extends Meldung<POS>, POS extends MeldungPosition> implements WbciBuilder<T> {

    protected String vorabstimmungsIdRef;
    protected WbciGeschaeftsfall wbciGeschaeftsfall;
    protected CarrierCode absender;
    protected WbciVersion wbciVersion = WbciVersion.V2;
    protected LocalDateTime processedAt;
    protected LocalDateTime sendAfter;
    protected IOType ioType;

    protected Set<POS> meldungsPositionen = new HashSet<>();

    protected void enrich(T meldung) {
        meldung.setVorabstimmungsIdRef(vorabstimmungsIdRef);
        meldung.setWbciGeschaeftsfall(wbciGeschaeftsfall);
        meldung.setAbsender(absender);
        meldung.setMeldungsPositionen(meldungsPositionen);
        meldung.setWbciVersion(wbciVersion);
        meldung.setProcessedAt(processedAt != null ? Date.from(processedAt.atZone(ZoneId.systemDefault()).toInstant()) : null);
        meldung.setIoType(ioType);
        meldung.setSendAfter(sendAfter != null ? Date.from(sendAfter.atZone(ZoneId.systemDefault()).toInstant()) : null);
    }

    public MeldungBuilder<T, POS> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        this.vorabstimmungsIdRef = vorabstimmungsIdRef;
        return this;
    }

    public MeldungBuilder<T, POS> withWbciGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall) {
        this.wbciGeschaeftsfall = wbciGeschaeftsfall;
        return this;
    }

    public MeldungBuilder<T, POS> withAbsender(CarrierCode absender) {
        this.absender = absender;
        return this;
    }

    public MeldungBuilder<T, POS> withWbciVersion(WbciVersion wbciVersion) {
        this.wbciVersion = wbciVersion;
        return this;
    }

    public MeldungBuilder<T, POS> withProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
        return this;
    }

    public MeldungBuilder<T, POS> withIoType(IOType ioType) {
        this.ioType = ioType;
        return this;
    }

    public MeldungBuilder<T, POS> withMeldungsPositionen(Set<POS> positionen) {
        this.meldungsPositionen = positionen;
        return this;
    }

    public MeldungBuilder<T, POS> addMeldungPosition(POS position) {
        this.meldungsPositionen.add(position);
        return this;
    }

    public MeldungBuilder<T, POS> withSendAfter(LocalDateTime sendAfter) {
        this.sendAfter = sendAfter;
        return this;
    }

}
